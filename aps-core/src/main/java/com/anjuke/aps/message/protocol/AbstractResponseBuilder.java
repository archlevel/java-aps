package com.anjuke.aps.message.protocol;

import java.util.Deque;
import java.util.List;

import com.anjuke.aps.exception.UnknownProtocolException;
import com.anjuke.aps.message.serializer.Serializer;

abstract class AbstractResponseBuilder implements ResponseBuilder {
    @Override
    public Response buildResponse(Deque<byte[]> frames, Serializer serializer) {
        byte[] headerFrame = frames.pollFirst();
        assertFrame(headerFrame, "Header Frame Missing");
        long sequence;
        double timestamp;
        int statusCode;
        try {
            List<Object> header = serializer.readValue(headerFrame);
            int index = 0;
            sequence = ((Number) header.get(index++)).longValue();
            timestamp = ((Number) header.get(index++)).doubleValue();
            statusCode = ((Number) header.get(index++)).intValue();
        } catch (Exception e) {
            throw new UnknownProtocolException("Unrecongnized Header Frame", e);
        }

        byte[] resultFrame = frames.pollFirst();
        Object result;
        if (resultFrame == null) {
            result = null;
        } else {
            try {
                result = serializer.readValue(resultFrame);
            } catch (Exception e) {
                throw new UnknownProtocolException(
                        "Unrecongnized Result Frame", e);
            }
        }
        return createResponse(sequence, timestamp, statusCode,
                result, frames, serializer);
    }

    abstract Response createResponse(long sequence, double timestamp,
            int status, Object result,
            Deque<byte[]> frames, Serializer serializer);

    private void assertFrame(byte[] frame, String message) {
        if (frame == null) {
            throw new UnknownProtocolException(message);
        }
    }

}
