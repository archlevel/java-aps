package com.anjuke.aps.client;

import org.zeromq.ZMQ;

public class ZMQInprocTest {
    public static void main(String[] args) {


        ZMQ.Context context1=ZMQ.context(1);
        ZMQ.Context context2=ZMQ.context(2);
//        ZMQ.Context context2=context1;

        String ep="inproc://test";
        ZMQ.Socket pull1=context1.socket(ZMQ.PULL);
        pull1.bind(ep);

        ZMQ.Socket push2=context2.socket(ZMQ.PUSH);
        push2.connect(ep);
        System.out.println("send");
        push2.send("123".getBytes());
        System.out.println("recv");
        System.out.println(new String(pull1.recv()));
    }
}
