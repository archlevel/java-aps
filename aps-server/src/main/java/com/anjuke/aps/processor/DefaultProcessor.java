package com.anjuke.aps.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.exception.ApsException;
import com.anjuke.aps.message.protocol.ApsStatus;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public class DefaultProcessor implements Processor {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultProcessor.class);
    private final List<RequestHandlerFilter> filterList = new ArrayList<RequestHandlerFilter>();

    private final List<RequestHandler> handlerList = new ArrayList<RequestHandler>();

    private Map<String, RequestHandler> methodMapping = new HashMap<String, RequestHandler>();

    public void addFilter(RequestHandlerFilter filter) {
        filterList.add(filter);
    }

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

        for (RequestHandlerFilter filter : filterList) {
            try {
                filter.init();
            } catch (Exception e) {
                throw new ApsException("RequestHandler init Error", e);
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

        for (RequestHandlerFilter filter : filterList) {
            boolean result = filter.beforeHandle(request, response);
            if (!result) {
                return;
            }
        }

        handler.handle(request, response);

        for (ListIterator<RequestHandlerFilter> iterator = filterList
                .listIterator(filterList.size()); iterator.hasPrevious();) {
            RequestHandlerFilter filter = iterator.previous();
            filter.afterHandler(request, response);
        }
    }

    @Override
    public synchronized void destory() {
        for (RequestHandlerFilter filter : filterList) {
            try {
                filter.destory();
            } catch (Exception e) {
                LOG.warn("Filter " + filter + " destory error", e);
            }
        }

        for (RequestHandler handler : handlerList) {
            try {
                handler.destory();
            } catch (Exception e) {
                LOG.warn("Handler " + handler + " destory error", e);
            }
        }
    }
}
