package com.anjuke.aps.message.protocol;

import java.util.Deque;

import com.anjuke.aps.message.serializer.Serializer;

interface ResponseBuilder {

    Response buildResponse(Deque<byte[]> frames, Serializer serializer);
}
