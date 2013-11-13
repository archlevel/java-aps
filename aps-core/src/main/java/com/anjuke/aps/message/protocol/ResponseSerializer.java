package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.message.serializer.Serializer;

public interface ResponseSerializer {

    public Deque<byte[]> serializeResponse(Response response,Serializer serializer);
}
