package com.anjuke.aps.kiteline.handler;

import java.util.Set;

import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;
import com.anjuke.aps.server.processor.RequestHandler;
import com.google.common.collect.ImmutableSet;

public class PingRequestHandler implements RequestHandler {

    @Override
    public void init() {

    }

    @Override
    public Set<String> getRequestMethods() {
        return ImmutableSet.of(".ping");
    }

    @Override
    public void handle(Request request, Response response) {
        response.setResult("pong");
    }

    @Override
    public void destory() {

    }
}
