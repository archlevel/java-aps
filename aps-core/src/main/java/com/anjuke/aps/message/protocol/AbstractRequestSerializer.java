package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.Lists;

abstract class AbstractRequestSerializer implements RequestSerializer {

    @Override
    public Deque<byte[]> serializeRequest(Request request, Serializer serializer) {
        LinkedList<byte[]> frame = Lists.newLinkedList();
        frame.offer(ProtocolFactory.APS_12_VERSION.getBytes());
        long sequence = request.getSequence();
        Object timestamp = getTimestamp(request.getRequestTimestamp());
        Object expire = getTimestamp(request.getExpiry());
        List<Object> header = Arrays.<Object> asList(sequence, timestamp,
                expire);
        frame.offer(serializer.writeValue(header));
        frame.offer(request.getRequestMethod().getBytes());
        frame.offer(serializer.writeValue(request.getRequestParams()));
        return appendFrames(request, serializer, frame);
    }

    abstract Object getTimestamp(long timestamp);

    abstract Deque<byte[]> appendFrames(Request request, Serializer serializer,
            Deque<byte[]> frames);
}
