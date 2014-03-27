package com.anjuke.test.single;

import java.util.Collections;
import java.util.Set;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.google.common.collect.Sets;

public class EchoRequestHandler implements RequestHandler {

    @Override
    public void init(ApsContext context) {

    }

    @Override
    public void destroy(ApsContext context) {

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
    public Set<ModuleVersion> getModules() {
        return Collections.emptySet();
    }

}
