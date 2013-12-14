package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.Response;
import com.anjuke.aps.message.serializer.Serializer;

interface ResponseSerializer {

    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer);
}
