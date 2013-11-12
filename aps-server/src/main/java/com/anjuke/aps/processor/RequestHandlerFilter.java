package com.anjuke.aps.processor;

import com.anjuke.aps.LifeCycle;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface RequestHandlerFilter extends LifeCycle {

    /**
     *
     * @param request
     * @param response
     * @return 返回false表示终止请求运行
     */
    public boolean beforeHandle(Request request,Response response);

    public void afterHandler(Request request,Response response);
}
