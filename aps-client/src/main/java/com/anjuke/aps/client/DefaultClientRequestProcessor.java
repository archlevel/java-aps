package com.anjuke.aps.client;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.exception.UnknownProtocolException;
import com.anjuke.aps.message.protocol.Protocol;
import com.anjuke.aps.message.protocol.ProtocolFactory;
import com.anjuke.aps.message.serializer.Serializer;
import com.anjuke.aps.util.ApsUtils;
import com.anjuke.aps.zmq.ZMQPollerRunner;
import com.anjuke.aps.zmq.ZMQUtils;
import com.anjuke.aps.zmq.ZMQWorkerSocketPool;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;

public class DefaultClientRequestProcessor implements
        ApsClientRequestProcessor, LifeCycle {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultClientRequestProcessor.class);

    private static final String ENDPOINT = "inproc://APS_CLIENT_WORKER_POOL";
    private static final int DEFAULT_TIMEOUT = 1000;
    private static final Iterable<byte[]> ENVELOP = Arrays
            .asList("".getBytes());

    private final List<String> endpoint;
    private final Serializer serializer;

    private ZMQ.Context context;
    private ZMQ.Socket workerSocket;
    private ZMQ.Socket connectorSocket;
    private ExecutorService threadPool;
    private ExecutorService pollerThreadPool;
    private ZMQWorkerSocketPool pool;
    private ConcurrentMap<Long, RequestEntry> requestCache;

    private volatile boolean running;

    public DefaultClientRequestProcessor(Serializer serializer,
            String... endpoints) {
        this.serializer = serializer;
        this.endpoint = Arrays.asList(endpoints);
    }

    @Override
    public void init(ApsContext apsContext) {
        running = true;
        context = ZMQ.context(1);
        workerSocket = context.socket(ZMQ.DEALER);
        workerSocket.setLinger(0);
        workerSocket.bind(ENDPOINT);
        connectorSocket = context.socket(ZMQ.DEALER);
        connectorSocket
                .setIdentity((ApsUtils.pid() + "@" + ApsUtils.hostname())
                        .getBytes());
        connectorSocket.setLinger(500);
        connectorSocket.setHWM(1000);
        for (String ep : endpoint) {
            connectorSocket.connect(ep);
        }
        ClientPoller poller = new ClientPoller(workerSocket, connectorSocket);
        pollerThreadPool = Executors.newSingleThreadExecutor(ApsUtils
                .threadFactory("ZMQDefaultClient-Poller"));
        pollerThreadPool.execute(poller);
        threadPool = Executors.newCachedThreadPool(ApsUtils
                .threadFactory("ZMQDefaultClient-Worker"));
        pool = new ZMQWorkerSocketPool(context, ENDPOINT);

        requestCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(50000)
                .<Long, RequestEntry> build().asMap();
    }

    @Override
    public void destroy() {
        running = false;
        pollerThreadPool.shutdown();
        try {
            pollerThreadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        pollerThreadPool.shutdownNow();
        threadPool.shutdownNow();

        connectorSocket.close();
        pool.destory();
        workerSocket.close();
        context.term();

    }

    @Override
    public Future<Response> request(Request request, Protocol protocol) {
        Deque<byte[]> frames = protocol.serializeRequest(request, serializer);

        ZMQ.Socket socket = pool.getSocket();

        final RequestEntry entry = new RequestEntry();
        requestCache.put(request.getSequence(), entry);
        try {
            ZMQUtils.sendMessage(socket,
                    Iterables.<byte[]> concat(ENVELOP, frames));
        } finally {
            pool.returnSocket(socket);
        }
        int timeoutMilliseconds = request.getExpiry();

        final int timeout = timeoutMilliseconds <= 0 ? DEFAULT_TIMEOUT
                : timeoutMilliseconds;

        Callable<Response> callable = new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                Response result = entry.get(timeout, TimeUnit.MILLISECONDS);

                if (result == null) {
                    throw new TimeoutException();
                }
                return result;
            }
        };
        return threadPool.submit(callable);

    }

    private class ClientPoller extends ZMQPollerRunner {

        private ClientPoller(Socket frontSocket, Socket backendSocket) {
            super(frontSocket, backendSocket);
        }

        @Override
        protected void handlerBackendIn(Socket frontSocket, Socket backendSocket) {
            Deque<byte[]> frames = ZMQUtils.receiveMessage(backendSocket);
            threadPool.execute(new ResponseWorker(frames));
        }

        @Override
        protected void handlerFrontIn(Socket frontSocket, Socket backendSocket) {
            ZMQUtils.forwardMessage(frontSocket, backendSocket);

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
            try {
                @SuppressWarnings("unused")
                List<byte[]> envelop = ZMQUtils.popEnvelop(recvData);
                Protocol protocol = ProtocolFactory.parseProtocol(recvData,
                        serializer);
                Response response = protocol.deserializeResponse(recvData,
                        serializer);
                long sequence = response.getSequence();
                RequestEntry entry = requestCache.get(sequence);
                if (entry == null) {
                    LOG.warn("Response with unkown sequence: " + sequence);
                    return;
                }
                entry.set(response);

            } catch (UnknownProtocolException e) {
                LOG.warn("Unkown protocol, response data dropped", e);
            } catch (Exception e) {
                LOG.warn("Process response data error", e);
            }
        }
    }

    private static class RequestEntry {
        private Response result;
        private CountDownLatch latch = new CountDownLatch(1);

        public Response get(int unit, TimeUnit timeunit)
                throws InterruptedException {
            latch.await(unit, timeunit);
            return result;
        }

        public void set(Response result) {
            this.result = result;
            latch.countDown();
        }
    }
}
