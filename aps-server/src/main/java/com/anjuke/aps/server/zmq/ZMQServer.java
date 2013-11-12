package com.anjuke.aps.server.zmq;

import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.message.MessageHandler;
import com.anjuke.aps.server.ApsServer;
import com.anjuke.aps.utils.ApsUtils;
import com.anjuke.aps.utils.ZMQUtils;

public class ZMQServer extends ApsServer {

    public static final String INPROC_ENDPOINT = "inproc://anjuke_aps_worker_endpoint";

    private static final Logger LOG = LoggerFactory.getLogger(ZMQServer.class);

    private final ExecutorService pollThreadPool = Executors
            .newCachedThreadPool(ApsUtils.threadFactory("ZMQServer-Poller"));

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
        String identity = hostname + ":" + port;
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
        try {
            pollThreadPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
        }
        serviceSocket.close();
        workerSocket.close();
    }

    private class PollerRunner implements Runnable {

        @Override
        public void run() {
            ZMQ.Poller poller = new ZMQ.Poller(2);
            poller.register(serviceSocket, ZMQ.Poller.POLLIN);
            poller.register(workerSocket, ZMQ.Poller.POLLIN);
            long waitTime = 1000;
            while (isRunning() && !Thread.currentThread().isInterrupted()) {
                try {
                    // wait while there are either requests or replies to
                    // process
                    if (poller.poll(waitTime) < 1) {
                        continue;
                    }

                    if (poller.pollin(0)) {
                        Deque<byte[]> frames = ZMQUtils
                                .receiveMessage(serviceSocket);
                        messageHandler.handlerMessage(new ZMQMessageChannel(frames,
                                workerSocketPool));
                    }

                    // process a reply
                    if (poller.pollin(1)) {
                        ZMQUtils.forwardMessage(workerSocket, serviceSocket);
                    }
                } catch (ZMQException e) {
                    // context destroyed, exit
                    if (ZMQ.Error.ETERM.getCode() == e.getErrorCode()) {
                        break;
                    }
                    LOG.error(e.getMessage(), e);
                    throw e;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

    }

}
