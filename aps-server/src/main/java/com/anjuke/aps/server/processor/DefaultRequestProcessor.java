package com.anjuke.aps.server.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.anjuke.aps.exception.ApsException;
import com.google.common.collect.Lists;

public class DefaultRequestProcessor implements RequestProcessor {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultRequestProcessor.class);
    private final List<RequestHandler> handlerList = new ArrayList<RequestHandler>();

    private Map<String, RequestHandler> methodMapping = new HashMap<String, RequestHandler>();


    public void addHandler(RequestHandler handler) {
        handlerList.add(handler);
    }

    @Override
    public synchronized void init() {
        for (RequestHandler handler : handlerList) {
            try {
                handler.init();
            } catch (Exception e) {
                throw new ApsException("RequestHandler init Error", e);
            }
            Set<String> methodSet = handler.getRequestMethods();
            for (String method : methodSet) {
                Object object = methodMapping.put(method, handler);
                if (object != null) {
                    throw new ApsException("Confilct method of " + method
                            + ", mapping 2 handler, " + object + " and "
                            + handler);
                }
            }
        }


        if (LOG.isInfoEnabled()) {
            List<String> urlList = Lists.newArrayList(methodMapping.keySet());
            Collections.sort(urlList);
            for (String url : urlList) {
                LOG.info("registered url " + url);
            }
        }
    }

    @Override
    public void process(Request request, Response response) {
        RequestHandler handler = methodMapping.get(request.getRequestMethod());
        if (handler == null) {
            response.setStatus(ApsStatus.METHOD_NOT_FOUND);
            response.setErrorMessage("Method Not Fount");
            return;
        }


        handler.handle(request, response);

        response.setResponseTimestamp(System.currentTimeMillis());
    }

    @Override
    public synchronized void destroy() {

        for (RequestHandler handler : handlerList) {
            try {
                handler.destroy();
            } catch (Exception e) {
                LOG.warn("Handler " + handler + " destory error", e);
            }
        }
    }
}
