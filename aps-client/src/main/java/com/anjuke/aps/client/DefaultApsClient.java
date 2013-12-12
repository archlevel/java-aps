package com.anjuke.aps.client;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.exception.ApsException;
import com.anjuke.aps.message.serializer.Serializer;
import com.anjuke.aps.util.ApsUtils;
import com.anjuke.aps.zmq.ZMQPollerRunner;
import com.anjuke.aps.zmq.ZMQUtils;
import com.anjuke.aps.zmq.ZMQWorkerSocketPool;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class DefaultApsClient implements ApsClient, LifeCycle {
    private static final String ENDPOINT = "inproc://APS_CLIENT_WORKER_POOL";
    private static final int DEFAULT_TIMEOUT=1000;


    private final List<String> endpoint;
    private final Serializer serializer;
    private final AtomicLong requestSequence = new AtomicLong();


    private ZMQ.Context context;
    private ZMQ.Socket workerSocket;
    private ZMQ.Socket connectorSocket;
    private ExecutorService threadPool;
    private ExecutorService pollerThreadPool;
    private ZMQWorkerSocketPool pool;
    private ConcurrentMap<Long, RequestEntry> requestCache;

    private volatile boolean running;

    public DefaultApsClient(Serializer serializer, String... endpoints) {
        this.serializer = serializer;
        this.endpoint = Arrays.asList(endpoints);
    }

    @Override
    public void init() {
        running = true;
        context = ZMQ.context(1);
        workerSocket = context.socket(ZMQ.DEALER);
        workerSocket.setLinger(0);
        workerSocket.bind(ENDPOINT);
        connectorSocket = context.socket(ZMQ.DEALER);
        connectorSocket.setIdentity((ApsUtils.pid()+"@"+ApsUtils.hostname()).getBytes());
        connectorSocket.setLinger(500);
        connectorSocket.setHWM(1000);
        for (String ep : endpoint) {
            connectorSocket.connect(ep);
        }
        ClientPoller poller = new ClientPoller(workerSocket, connectorSocket);
        pollerThreadPool = Executors.newSingleThreadExecutor(ApsUtils.threadFactory("ZMQDefaultClient-Poller"));
        pollerThreadPool.execute(poller);
        threadPool = Executors.newCachedThreadPool(ApsUtils.threadFactory("ZMQDefaultClient-Worker"));
        pool = new ZMQWorkerSocketPool(context, ENDPOINT);

        requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(50000)
                .<Long, RequestEntry> build().asMap();
    }

    @Override
    public void destory() {
        running = false;
        pollerThreadPool.shutdown();
        try {
            pollerThreadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        pollerThreadPool.shutdownNow();
        threadPool.shutdownNow();
        try {
            connectorSocket.close();
            pool.destory();
            workerSocket.close();
        } finally {
            context.term();
        }
    }

    @Override
    public Future<Object> asyncRequest(final String url,
            final int timeoutMilliseconds, final Object... args) {
        if (!running) {
            throw new ApsException("Client already shutdown");
        }
        long id = requestSequence.getAndIncrement();
        LinkedList<byte[]> data = Lists.newLinkedList();
        data.add("".getBytes());
        data.add("APS12".getBytes());
        data.add(serializer.writeValue(Arrays.<Object> asList(id,
                System.currentTimeMillis() / 1000, timeoutMilliseconds / 1000.0)));
        data.add(url.getBytes());
        data.add(serializer.writeValue(Arrays.asList(args)));
        final RequestEntry entry = new RequestEntry();
        requestCache.put(id, entry);

        ZMQ.Socket socket = pool.getSocket();
        try {
            ZMQUtils.sendMessage(socket, data);
        } finally {
            pool.returnSocket(socket);
        }

        final int timeout=timeoutMilliseconds<=0? DEFAULT_TIMEOUT:timeoutMilliseconds;

        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Optional<Object> result = entry.get(timeout,
                        TimeUnit.MILLISECONDS);

                if (result == null) {
                    throw new TimeoutException();
                }
                return result.get();
            }
        };
        return threadPool.submit(callable);
    }

    @Override
    public Object request(final String url, final int timeoutMilliseconds,
            final Object... args) throws TimeoutException,
            InterruptedException, ExecutionException {
        if (!running) {
            throw new ApsException("Client already shutdown");
        }
        return asyncRequest(url, timeoutMilliseconds, args).get();
    }

    private class ClientPoller extends ZMQPollerRunner {

        private ClientPoller(Socket frontSocket, Socket backendSocket) {
            super(frontSocket, backendSocket);
        }

        @Override
        protected void handlerBackendIn(Socket frontSocket, Socket backendSocket) {
            Deque<byte[]> frames=ZMQUtils.receiveMessage(backendSocket);
            threadPool.execute(new ResponseWorker(frames));
        }

        @Override
        protected void handlerFrontIn(Socket frontSocket, Socket backendSocket) {
            ZMQUtils.forwardMessage(frontSocket,backendSocket);

        }

        @Override
        protected boolean isRunning() {
            return running;
        }

    }


    private class ResponseWorker implements Runnable {
        private final Deque<byte[]> recvData;

        private ResponseWorker(Deque<byte[]> frames) {
            this.recvData = frames;
        }

        @Override
        public void run() {
              byte[] empty=recvData.poll();
              byte[] version=recvData.poll();
              List<Object> header=serializer.readValue(recvData.poll());
              Long sequence=((Number) header.get(0)).longValue();
              if(((Number)header.get(2)).intValue()==200){
                  Object result= serializer.readValue(recvData.poll());
                  RequestEntry entry=requestCache.get(sequence);
                  entry.set(result);
              }else{
                  throw new ApsException("status code: "+header.get(3));
              }
        }
    }

    private static class RequestEntry {
        private Optional<Object> result;
        private CountDownLatch latch = new CountDownLatch(1);

        public Optional<Object> get(int unit, TimeUnit timeunit)
                throws InterruptedException {
            latch.await(unit, timeunit);
            return result;
        }

        public void set(Object result) {
            this.result = Optional.fromNullable(result);
            latch.countDown();
        }
    }

}
