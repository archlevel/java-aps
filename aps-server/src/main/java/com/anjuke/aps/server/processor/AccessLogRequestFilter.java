package com.anjuke.aps.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.message.MessageFilter;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public class AccessLogRequestFilter implements MessageFilter {

    private static final Logger LOG = LoggerFactory
            .getLogger("ACCESS_LOG");

    @Override
    public void init() {
    }

    @Override
    public void destory() {
    }

    @Override
    public boolean beforeHandle(Request request, Response response) {
        return true;
    }

    @Override
    public void afterHandler(Request request, Response response) {
        LOG.info("{} - {} - {} - {}",response.getResponseTimestamp(),
                request.getRequestMethod(), request.getSequence(),
                response.getStatus());
    }

}
