package com.anjuke.aps.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.RunnableComponent;
import com.anjuke.aps.message.MessageHandler;
import com.anjuke.aps.util.Asserts;

public abstract class ApsServer implements RunnableComponent{
    private volatile boolean running;

    private volatile CountDownLatch shutdownLatch;

    private List<ApsServerStatusListener> serverStatusListeners = Collections
            .synchronizedList(new ArrayList<ApsServerStatusListener>());

    private MessageHandler messageHandler;

    private final ApsContext context=new ApsContext();

    @Override
    public boolean isRunning() {
        return running;
    }

    public void addServerStatusListener(ApsServerStatusListener listener){
        serverStatusListeners.add(listener);
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    protected abstract void initialize(ApsContext context,MessageHandler messageHandler);

    protected abstract void doStart();

    protected abstract void doStop();

    protected abstract void destroy();

    private void init() {
        Asserts.notNull(messageHandler, "MessageHandler must not be null");
        messageHandler.init(context);
        initialize(context,messageHandler);
    }

    @Override
    public void start() {
        init();

        callBeforeStartListener();
        running = true;
        shutdownLatch = new CountDownLatch(1);
        doStart();

        callAfterStartListener();

    }

    public void join() throws InterruptedException {
        shutdownLatch.await();
    }

    @Override
    public void stop() {
        callBeforeShutdownListener();
        running = false;
        doStop();
        callAfterShutdownListerner();
        messageHandler.destroy(context);
        destroy();
        shutdownLatch.countDown();
    }

    private void callBeforeStartListener() {
        for (ApsServerStatusListener listener : serverStatusListeners) {
            listener.beforeStart(context);
        }
    }

    private void callAfterStartListener() {
        for (ApsServerStatusListener listener : serverStatusListeners) {
            listener.afterStart(context);
        }
    }

    private void callBeforeShutdownListener() {
        for (ApsServerStatusListener listener : serverStatusListeners) {
            listener.beforeStop(context);
        }
    }

    private void callAfterShutdownListerner() {
        for (ApsServerStatusListener listener : serverStatusListeners) {
            listener.afterStop(context);
        }

    }


}
