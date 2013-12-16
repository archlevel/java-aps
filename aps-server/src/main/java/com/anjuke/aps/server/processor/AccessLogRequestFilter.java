package com.anjuke.aps.server.processor;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.ExtraMessage;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.MessageFilter;
import com.google.common.base.Joiner;

public class AccessLogRequestFilter implements MessageFilter {

    private static final Logger LOG = LoggerFactory.getLogger("ACCESS_LOG");

    private static final ThreadLocal<Long> START_REQUEST_TIME = new ThreadLocal<Long>();

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean beforeHandle(Request request, Response response) {
        START_REQUEST_TIME.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterHandler(Request request, Response response) {
        Collection<Object> sender = request.getExtra(ExtraMessage.SENDER);
        ;
        String senderString = (sender == null || sender.isEmpty()) ? "unkown"
                : (Joiner.on(",").join(sender));
        long time = System.currentTimeMillis() - START_REQUEST_TIME.get();
        LOG.info("{} - {} - {} - {} - {}", senderString, time,
                request.getRequestMethod(), request.getSequence(),
                response.getStatus());
        START_REQUEST_TIME.remove();
    }

}
