package com.anjuke.aps.zmq;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zeromq.ZMQ;

import com.google.common.collect.Lists;

public abstract class ZMQUtils {
    private ZMQUtils() {
    }

    public static void forwardMessage(ZMQ.Socket inSocket, ZMQ.Socket outSocket) {
        boolean more = true;
        while (more) {
            byte[] msg = inSocket.recv(0);

            more = inSocket.hasReceiveMore();

            if (msg != null) {
                outSocket.send(msg, more ? ZMQ.SNDMORE : 0);
            }
        }
    }

    public static void sendMessage(ZMQ.Socket socket, Iterable<byte[]> frames) {
        Iterator<byte[]> iterator = frames.iterator();
        while (iterator.hasNext()) {
            byte[] data=iterator.next();
            int more=iterator.hasNext() ? ZMQ.SNDMORE : 0;
            socket.send(data, more);
        }
    }

    public static Deque<byte[]> receiveMessage(ZMQ.Socket socket) {
        Deque<byte[]> list = new LinkedList<byte[]>();
        do {
            byte[] data = socket.recv(0);
            if (data == null) {
                break;
            }
            list.add(data);
        } while (socket.hasReceiveMore());
        return list;
    }

    /**
     *
     * @param frames
     *            Data need to parse envelop, the frames may be changed by this
     *            method
     * @return the envelop
     */
    public static List<byte[]> popEnvelop(Deque<byte[]> frames) {
        List<byte[]> envelop = Collections.emptyList();
        LinkedList<byte[]> tmp = Lists.newLinkedList();
        for (byte[] frame : frames) {
            tmp.add(frame);

            if (frame.length == 0) {
                if (envelop.size() == 0) {
                    envelop = Lists.newLinkedList();
                }
                envelop.addAll(tmp);
                tmp.clear();
            }
        }
        for(int i=0;i<envelop.size();i++){
            frames.removeFirst();
        }
        return envelop;
    }
}
