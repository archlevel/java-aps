package com.anjuke.aps.zmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public abstract class ZMQPollerRunner implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final ZMQ.Socket frontSocket;
    private final ZMQ.Socket backendSocket;

    protected ZMQPollerRunner(ZMQ.Socket frontSocket, ZMQ.Socket backendSocket) {
        super();
        this.frontSocket = frontSocket;
        this.backendSocket = backendSocket;
    }

    protected abstract boolean isRunning();

    protected abstract void handlerFrontIn(ZMQ.Socket frontSocket,
            ZMQ.Socket backendSocket);

    protected abstract void handlerBackendIn(ZMQ.Socket frontSocket,
            ZMQ.Socket backendSocket);

    @Override
    public void run() {

        ZMQ.Poller poller = new ZMQ.Poller(2);
        poller.register(frontSocket, ZMQ.Poller.POLLIN);
        poller.register(backendSocket, ZMQ.Poller.POLLIN);
        long waitTime = 1000;
        while (isRunning() && !Thread.currentThread().isInterrupted()) {
            try {
                // wait while there are either requests or replies to
                // process
                if (poller.poll(waitTime) < 1) {
                    continue;
                }

                if (poller.pollin(0)) {
                    handlerFrontIn(frontSocket, backendSocket);
                }

                // process a reply
                if (poller.pollin(1)) {
                    handlerBackendIn(frontSocket, backendSocket);
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
