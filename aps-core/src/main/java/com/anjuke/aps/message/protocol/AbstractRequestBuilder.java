package com.anjuke.aps.message.protocol;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.anjuke.aps.Request;
import com.anjuke.aps.exception.UnknownProtocolException;
import com.anjuke.aps.message.serializer.Serializer;

abstract class AbstractRequestBuilder implements RequestBuilder {
    @Override
    public Request buildRequest(Deque<byte[]> frames, Serializer serializer) {
        byte[] headerFrame = frames.pollFirst();
        assertFrame(headerFrame, "Header Frame Missing");
        long sequence;
        double timestamp;
        double expire;
        try {
            List<Object> header = serializer.readValue(headerFrame);
            int index = 0;
            sequence = ((Number) header.get(index++)).longValue();
            timestamp = ((Number) header.get(index++)).doubleValue();
            expire = ((Number) header.get(index++)).doubleValue();
        } catch (Exception e) {
            throw new UnknownProtocolException("Unrecongnized Header Frame", e);
        }
        byte[] requestMethodFrame = frames.pollFirst();
        assertFrame(requestMethodFrame, "Method Frame Missing");

        String requestMethod = serializer.readString(requestMethodFrame);

        byte[] paramsFrame = frames.pollFirst();
        List<Object> params;
        if (paramsFrame == null) {
            params = Collections.emptyList();
        } else {
            try {
                params = serializer.readValue(paramsFrame);
            } catch (Exception e) {
                throw new UnknownProtocolException(
                        "Unrecongnized Parameter Frame", e);
            }
        }

        return createRequest(sequence, timestamp, expire, requestMethod,
                params, frames,serializer);
    }

    abstract Request createRequest(long sequence, double timestamp, double expire,
            String requestMethod, List<Object> params, Deque<byte[]> frames,
            Serializer serializer);

    private void assertFrame(byte[] frame, String message) {
        if (frame == null) {
            throw new UnknownProtocolException(message);
        }
    }

}
