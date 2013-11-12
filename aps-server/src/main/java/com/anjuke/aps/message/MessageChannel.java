package com.anjuke.aps.message;

import java.util.Deque;

public interface MessageChannel {
    public Deque<byte[]> receive();

    public void send(Deque<byte[]> frames);
}
