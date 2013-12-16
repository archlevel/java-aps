package com.anjuke.test.single;

import java.util.Collections;
import java.util.Set;

import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.google.common.collect.Sets;

public class EchoRequestHandler implements RequestHandler {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Set<String> getRequestMethods() {
        return Sets.newHashSet("echo");
    }

    @Override
    public void handle(Request request, Response response) {
        response.setResult(request.getRequestParams());
    }

    @Override
    public Set<String> getModules() {
        return Collections.emptySet();
    }

}
