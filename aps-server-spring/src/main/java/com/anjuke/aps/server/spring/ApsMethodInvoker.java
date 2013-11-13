package com.anjuke.aps.server.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class ApsMethodInvoker {
    private final Object targetBean;
    private final Method method;
    //动态代理对象会丢失方法签名的泛型信息进而导致错误，因此用registerMethod保存@ApiModule里声明的method信息
    //getGenericParameterTypes从registerMethod返回，但是invoke时还是从对象真正的方法method上执行
    private final Method registerMethod;

    ApsMethodInvoker(Object targetBean, Method method,Method registerMethod) {
        super();
        this.targetBean = targetBean;
        this.method = method;
        this.registerMethod=registerMethod;
    }

    public Object invoke(Object... parames) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        return method.invoke(targetBean, parames);
    }

    public Type[] getGenericParameterTypes(){
        return registerMethod.getGenericParameterTypes();
    }

}
