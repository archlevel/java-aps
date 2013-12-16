package com.anjuke.aps.server.processor;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;

public interface RequestProcessor extends LifeCycle {

    public void process(Request request,Response response);
}
