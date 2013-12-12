package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.message.serializer.Serializer;

public interface Protocol {

    public Request prepareRequest(long sequence, String requestMethod,
            int expire, Object... params);

    public Deque<byte[]> serializeRequest(Request request, Serializer serializer);

    public Request deserializeRequest(Deque<byte[]> frame, Serializer serializer);

    public Response prepareResponse(Request request);

    public Response deserializeResponse(Deque<byte[]> frame,Serializer serializer);

    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer);

}
