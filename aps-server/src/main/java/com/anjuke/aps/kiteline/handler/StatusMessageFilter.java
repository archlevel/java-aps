package com.anjuke.aps.kiteline.handler;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.MessageFilter;

public class StatusMessageFilter implements MessageFilter {
    private StatusContext context;

    public StatusContext getContext() {
        return context;
    }

    public void setContext(StatusContext context) {
        this.context = context;
    }

    @Override
    public void init(ApsContext context) {

    }

    @Override
    public void destroy(ApsContext context) {

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
