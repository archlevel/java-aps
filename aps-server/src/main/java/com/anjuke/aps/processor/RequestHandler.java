package com.anjuke.aps.processor;

import java.util.Set;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface RequestHandler extends LifeCycle {

    /**
     * 会在init()之后调用该方法
     * @return Request methods of this handler supported;
     */
    public Set<String> getRequestMethods();

    public void handle(Request request,Response response);
}
