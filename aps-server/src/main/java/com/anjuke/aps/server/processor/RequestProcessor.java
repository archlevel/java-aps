package com.anjuke.aps.server.processor;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface RequestProcessor extends LifeCycle {

    public void process(Request request,Response response);
}
