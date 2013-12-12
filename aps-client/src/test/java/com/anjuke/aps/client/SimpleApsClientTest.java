package com.anjuke.aps.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anjuke.aps.message.protocol.ProtocolFactory;
import com.anjuke.aps.message.serializer.MessagePackSerializer;

public class SimpleApsClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

    }

    public static void main(String[] args) throws TimeoutException,
            InterruptedException, ExecutionException {
        ApsClient client;

        DefaultRequestProcessor processor = new DefaultRequestProcessor(
                new MessagePackSerializer(), "tcp://127.0.0.1:8964");

        processor.init();

        client = new SimpleApsClient(
                ProtocolFactory.getProtocol(ProtocolFactory.APS_12_VERSION),
                processor);

        // client=new SimpleApsClient("tcp://192.168.1.62:1123", new
        // MessagePackSerializer());
        System.out.println(client.request(".ping", 1000));

        // DefaultApsClient defClient=new DefaultApsClient(new
        // MessagePackSerializer(),"tcp://127.0.0.1:8964");
        // defClient.init();
        // for(int i=0;i<100;i++){
        // Future<Object> f= defClient.asyncRequest(".ping", 1000);
        // }
        // defClient.destory();

        processor.destory();
    }
}
