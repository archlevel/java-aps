package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.message.serializer.Serializer;
import com.anjuke.aps.util.Asserts;

abstract class AbstractResponseSerializer implements ResponseSerializer {
    @Override
    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer) {
        Deque<byte[]> frames = new LinkedList<byte[]>();
        frames.offer(ProtocolFactory.APS_10_VERSION.getBytes());
        int status = response.getStatus();
        List<? extends Object> header = Arrays.<Object> asList(
                response.getSequence(),
                getTimestamp(response.getResponseTimestamp()), status);
        byte[] headerBytes = serializer.writeValue(header);
        frames.offer(headerBytes);

        if (status == ApsStatus.SUCCESS) {
            Object result = response.getResult();
            Asserts.allowedType(result);
            frames.offer(serializer.writeValue(result));
        } else {
            frames.offer(serializer.writeValue(null));
        }
        return appendFrames(response, serializer, frames);
    }

    abstract Object getTimestamp(long timestamp);

    abstract Deque<byte[]> appendFrames(Response response,
            Serializer serializer, Deque<byte[]> frames);
}
