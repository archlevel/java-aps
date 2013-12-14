package com.anjuke.aps.kiteline.handler;

import java.util.Collections;
import java.util.Set;

import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
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
    public void destroy() {

    }

    @Override
    public Set<String> getModules() {

        return Collections.emptySet();
    }
}
