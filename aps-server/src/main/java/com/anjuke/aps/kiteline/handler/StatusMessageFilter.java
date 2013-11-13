package com.anjuke.aps.kiteline.handler;

import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.message.MessageFilter;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public class StatusMessageFilter implements MessageFilter {
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
    public boolean beforeHandle(Request request, Response response) {
        context.incRequests();
        return true;
    }

    @Override
    public void afterHandler(Request request, Response response) {
        if(response.getStatus()!=ApsStatus.SUCCESS){
            context.incExceptions();
        }
    }

    @Override
    public String toString() {
        return "StatusMessageHandler [context=" + context + "]";
    }

}
