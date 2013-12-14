package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.Request;
import com.anjuke.aps.message.serializer.Serializer;

interface RequestSerializer {
    public Deque<byte[]> serializeRequest(Request request,
            Serializer serializer);
}
