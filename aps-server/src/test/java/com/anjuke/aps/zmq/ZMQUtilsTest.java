package com.anjuke.aps.zmq;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zeromq.ZMQ;

import com.anjuke.aps.zmq.ZMQUtils;

public class ZMQUtilsTest {

    private static final String ENDPOINT = "inproc://zmq_util_unit_test";
    private static ZMQ.Context context;

    @BeforeClass
    public static void beforeClass() {
        context = ZMQ.context(1);
    }

    @AfterClass
    public static void afterClass() {
        context.term();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSendMessageAndReceiveMessage() {
        ZMQ.Socket sendSocket = context.socket(ZMQ.DEALER);
        sendSocket.bind(ENDPOINT);
        ZMQ.Socket receiveSocket = context.socket(ZMQ.DEALER);
        receiveSocket.connect(ENDPOINT);

        try {
            List<byte[]> data=Arrays.asList(new byte[]{1},new byte[]{2});
            ZMQUtils.sendMessage(sendSocket, data);
            Deque<byte[]> receivedData=ZMQUtils.receiveMessage(receiveSocket);
            int i=0;
            Assert.assertEquals(data.size(),receivedData.size());
            for(byte[] frame:receivedData){
                Assert.assertArrayEquals(data.get(i),frame);
                i++;
            }
        } finally {
            sendSocket.close();
            receiveSocket.close();
        }
    }

    @Test
    public void testForwardMessage() {
        ZMQ.Socket frontSocket = context.socket(ZMQ.DEALER);
        frontSocket.bind(ENDPOINT+"-1");
        ZMQ.Socket sendSocket = context.socket(ZMQ.DEALER);
        sendSocket.connect(ENDPOINT+"-1");

        ZMQ.Socket backSocket = context.socket(ZMQ.DEALER);
        backSocket.bind(ENDPOINT+"-2");
        ZMQ.Socket receiveSocket = context.socket(ZMQ.DEALER);
        receiveSocket.connect(ENDPOINT+"-2");

        try {
            List<byte[]> data=Arrays.asList(new byte[]{1},new byte[]{2});
            ZMQUtils.sendMessage(sendSocket, data);
            ZMQUtils.forwardMessage(frontSocket, backSocket);
            Deque<byte[]> receivedData=ZMQUtils.receiveMessage(receiveSocket);
            int i=0;
            Assert.assertEquals(data.size(),receivedData.size());
            for(byte[] frame:receivedData){
                Assert.assertArrayEquals(data.get(i),frame);
                i++;
            }
        } finally {
            frontSocket.close();
            backSocket.close();
            sendSocket.close();
            receiveSocket.close();
        }
    }
}
