package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.message.serializer.Serializer;

public interface Protocol {

    public Request deserializeRequest(Deque<byte[]> frame,Serializer serializer);

    public Response prepareResponse(Request request);

    public Deque<byte[]> serializeResponse(Response response,Serializer serializer);
}
