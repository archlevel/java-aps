package com.anjuke.aps;

import java.util.Set;

public interface RequestHandler {

    public void init();

    public void destroy();

    /**
     * 会在init()之后调用该方法
     * @return Request methods of this handler supported;
     */
    public Set<String> getRequestMethods();

    /**
     * 会在init()之后调用该方法
     * 返回该对象所支持的module，对应于kite-line里的SP
     * 没有则返回Collections.emptySet();
     * @return
     */
    public Set<String> getModules();


    public void handle(Request request,Response response);
}
