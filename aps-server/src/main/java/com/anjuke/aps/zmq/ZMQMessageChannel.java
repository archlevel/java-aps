package com.anjuke.aps.zmq;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.zeromq.ZMQ;

import com.anjuke.aps.message.MessageChannel;
import com.google.common.collect.Iterables;

class ZMQMessageChannel implements MessageChannel {
    public static final byte[] EMPTY_FRAME = "".getBytes();
    private final Deque<byte[]> data;

    private final List<byte[]> envelop;

    private final ZMQWorkerSocketPool pool;

    ZMQMessageChannel(Deque<byte[]> data, ZMQWorkerSocketPool pool) {
        this.envelop = parseEnvelop(data);
        this.data = data;
        this.pool = pool;
    }

    @Override
    public Deque<byte[]> receive() {
        return data;
    }

    @Override
    public void send(Deque<byte[]> frames) {
        ZMQ.Socket socket = pool.getSocket();
        try {
            ZMQUtils.sendMessage(socket, Iterables.concat(envelop, frames));
        } finally {
            pool.returnSocket(socket);
        }
    }

    private List<byte[]> parseEnvelop(Deque<byte[]> frames) {
        List<byte[]> envelop = new LinkedList<byte[]>();
        byte[] frame = frames.poll();
        while (frame != null && !Arrays.equals(EMPTY_FRAME, frame)) {
            envelop.add(frame);
            frame = frames.poll();
        }
        if (frame == null) {
            throw new IllegalStateException("envelop parse error");
        }
        envelop.add(frame);
        return envelop;

    }

}
