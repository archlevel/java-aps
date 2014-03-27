package com.anjuke.aps.kiteline.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class StatusRequestHandler implements RequestHandler {
    private StatusContext context;
    private Set<String> urlSet=Sets.newHashSet();

    public StatusContext getContext() {
        return context;
    }

    public void setContext(StatusContext context) {
        this.context = context;
    }

    @Override
    public void init(ApsContext context) {
         urlSet.add(".status");
         Set<ModuleVersion> set=context.getAttribute(ApsContext.LOAD_MODULE_KEY);
         for(ModuleVersion mv:set){
             urlSet.add(":"+mv.getName()+":.status");
         }
    }

    @Override
    public void destroy(ApsContext context) {

    }

    @Override
    public Set<ModuleVersion> getModules() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRequestMethods() {
        return Collections.unmodifiableSet(urlSet);
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
