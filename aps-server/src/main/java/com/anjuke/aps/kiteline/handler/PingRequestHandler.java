package com.anjuke.aps.kiteline.handler;

import java.util.Collections;
import java.util.Set;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.google.common.collect.Sets;

public class PingRequestHandler implements RequestHandler {

    private Set<String> urlSet=Sets.newHashSet();
    @Override
    public void init(ApsContext context) {
        urlSet.add(".ping");
        Set<ModuleVersion> set=context.getAttribute(ApsContext.LOAD_MODULE_KEY);
        for(ModuleVersion mv:set){
            urlSet.add(":"+mv.getName()+":.ping");
        }
    }

    @Override
    public Set<String> getRequestMethods() {
        return Collections.unmodifiableSet(urlSet);
    }

    @Override
    public void handle(Request request, Response response) {
        response.setResult("pong");
    }

    @Override
    public void destroy(ApsContext context) {

    }

    @Override
    public Set<ModuleVersion> getModules() {

        return Collections.emptySet();
    }
}
