package com.anjuke.aps.zmq;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.zeromq.ZMQ;


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
            socket.send(iterator.next(), iterator.hasNext() ? ZMQ.SNDMORE : 0);
        }
    }

    public static Deque<byte[]> receiveMessage(ZMQ.Socket socket) {
        Deque<byte[]> list = new LinkedList<byte[]>();
        do {
            list.add(socket.recv(0));
        } while (socket.hasReceiveMore());
        return list;
    }
}
