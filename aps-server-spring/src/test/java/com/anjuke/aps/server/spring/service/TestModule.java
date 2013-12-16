package com.anjuke.aps.server.spring.service;

import java.util.Map;

import com.anjuke.aps.spring.ApsMethod;
import com.anjuke.aps.spring.ApsModule;

@ApsModule(name="javatest")
public interface TestModule {

    @ApsMethod(bean="testService",method="echo")
    public  String echo(String message);

    @ApsMethod(bean="testService",method="echoMap")
    public  Map<String,Object> echoMap(Map<String,Object> message);
}