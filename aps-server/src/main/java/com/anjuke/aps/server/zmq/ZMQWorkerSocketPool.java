package com.anjuke.aps.server.zmq;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.google.common.collect.Lists;

public class ZMQWorkerSocketPool {
    private static final ThreadLocal<ZMQ.Socket> localSocketHolder = new ThreadLocal<ZMQ.Socket>();

    private final List<ZMQ.Socket> socketRefs = Collections
            .synchronizedList(Lists.<ZMQ.Socket> newArrayList());

    private final ZMQ.Context context;
    private final AtomicInteger usedSocketCount = new AtomicInteger();

    private final String endpoint;

    ZMQWorkerSocketPool(Context context, String endpoint) {
        super();
        this.context = context;
        this.endpoint = endpoint;
    }

    public Socket getSocket() {
        ZMQ.Socket socket=localSocketHolder.get();
        if(socket==null){
            socket=context.socket(ZMQ.DEALER);
            socket.connect(endpoint);
            localSocketHolder.set(socket);
            socketRefs.add(socket);
        }
        usedSocketCount.incrementAndGet();
        return socket;
    }

    public void returnSocket(Socket socket) {
        usedSocketCount.decrementAndGet();
    }

    public void destory() {
        while(usedSocketCount.get()>0){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
            continue;
        }

        for(ZMQ.Socket socket:socketRefs){
            socket.close();
        }
    }

}
