package com.anjuke.aps.kiteline.handler;

import java.util.Map;
import java.util.Set;

import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;
import com.anjuke.aps.server.processor.RequestHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class StatusRequestHandler implements RequestHandler {
    private StatusContext context;

    public StatusContext getContext() {
        return context;
    }

    public void setContext(StatusContext context) {
        this.context = context;
    }

    @Override
    public void init() {

    }

    @Override
    public void destory() {

    }

    @Override
    public Set<String> getRequestMethods() {
        return ImmutableSet.of(".status");
    }

    @Override
    public void handle(Request request, Response response) {
        Map<String, Object> result = ImmutableMap.<String, Object> of("uptime",
                context.getUpTime(), "requests", context.getRequests(),
                "pendings", context.getPendings(), "exceptions",
                context.getExceptions());
        response.setResult(result);
    }

    @Override
    public String toString() {
        return "StatusRequestHandler [context=" + context + "]";
    }

}
