package com.anjuke.aps.server.zmq;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.anjuke.aps.message.MessageHandler;
import com.anjuke.aps.server.ApsServer;
import com.anjuke.aps.util.ApsUtils;
import com.anjuke.aps.zmq.ZMQPollerRunner;
import com.anjuke.aps.zmq.ZMQUtils;
import com.anjuke.aps.zmq.ZMQWorkerSocketPool;

public class ZMQServer extends ApsServer {

    private static final Logger LOG = LoggerFactory.getLogger(ZMQServer.class);

    private static final String INPROC_ENDPOINT = "inproc://anjuke_aps_worker_endpoint";

    private final ExecutorService pollThreadPool = Executors
            .newCachedThreadPool(ApsUtils.threadFactory("ZMQServer-Poller"));

    private final ExecutorService workerThreadPool = Executors
            .newCachedThreadPool(ApsUtils.threadFactory("ZMQServer-Worker"));

    private String hostname = ApsUtils.hostname();

    private int port = 9227;

    private int zmqHWM = 1000;

    private int zmqLinger = 500;

    private ZMQ.Context context;

    private ZMQ.Socket serviceSocket;

    private ZMQ.Socket workerSocket;

    private ZMQWorkerSocketPool workerSocketPool;

    private MessageHandler messageHandler;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getZmqHWM() {
        return zmqHWM;
    }

    public void setZmqHWM(int zmqHWM) {
        this.zmqHWM = zmqHWM;
    }

    public int getZmqLinger() {
        return zmqLinger;
    }

    public void setZmqLinger(int zmqLinger) {
        this.zmqLinger = zmqLinger;
    }

    @Override
    protected void initialize(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    protected void doStart() {
        bindSocket();
        initPoller();
    }

    private void bindSocket() {
        context = ZMQ.context(1);
        serviceSocket = context.socket(ZMQ.ROUTER);
        String identity = "tcp://" + hostname + ":" + port;
        String endpoint = "tcp://*:" + port;
        serviceSocket.setIdentity(identity.getBytes());
        serviceSocket.setLinger(zmqLinger);
        serviceSocket.setHWM(zmqHWM);
        if (serviceSocket.bind("tcp://*:" + port) <= 0) {
            serviceSocket.close();
            context.term();
            throw new IllegalStateException("service port binding fail");
        }
        LOG.info("Using identity " + identity + " wait for response on "
                + endpoint);

        workerSocket = context.socket(ZMQ.DEALER);
        workerSocket.setLinger(0);
        workerSocket.bind(INPROC_ENDPOINT);

        workerSocketPool = new ZMQWorkerSocketPool(context, INPROC_ENDPOINT);
    }

    private void initPoller() {
        pollThreadPool.execute(new PollerRunner());
    }

    @Override
    protected void doStop() {
    }

    @Override
    protected void destroy() {
        pollThreadPool.shutdown();
        try {
            pollThreadPool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        workerThreadPool.shutdown();
        try{
            workerThreadPool.awaitTermination(30, TimeUnit.SECONDS);
        }catch(InterruptedException e){

        }
        pollThreadPool.shutdownNow();
        workerThreadPool.shutdownNow();


        workerSocketPool.destory();
        serviceSocket.close();
        workerSocket.close();
        context.term();
    }

    private class PollerRunner extends ZMQPollerRunner {

        public PollerRunner() {
            super(serviceSocket, workerSocket);
        }

        @Override
        protected boolean isRunning() {
            return ZMQServer.this.isRunning();
        }

        @Override
        protected void handlerFrontIn(Socket frontSocket, Socket backendSocket) {
            final Deque<byte[]> frames = ZMQUtils.receiveMessage(frontSocket);
            workerThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    messageHandler.handlerMessage(new ZMQMessageChannel(frames,
                            workerSocketPool));

                }
            });
        }

        @Override
        protected void handlerBackendIn(Socket frontSocket, Socket backendSocket) {
            ZMQUtils.forwardMessage(backendSocket, frontSocket);

        }

    }

}
