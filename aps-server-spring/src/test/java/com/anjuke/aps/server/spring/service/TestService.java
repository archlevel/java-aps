package com.anjuke.aps.server.spring.service;

import java.util.Map;

public class TestService {

    public String echo(String msg){
        return msg;
    }

    public  Map<String,Object> echoMap(Map<String,Object> message){
        return message;
    }
}
