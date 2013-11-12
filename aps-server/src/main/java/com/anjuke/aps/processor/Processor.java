package com.anjuke.aps.processor;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface Processor extends LifeCycle {

    public void process(Request request,Response response);
}
