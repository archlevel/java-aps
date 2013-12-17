package com.anjuke.aps.server;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.message.MessageChannel;
import com.anjuke.aps.message.MessageHandler;

public class ApsServerTest {

    final int init = 0;
    final int beforeStart = 1;
    final int doStart = 2;
    final int afterStart = 3;
    final int beforeShutdown = 4;
    final int doShutdown = 5;
    final int afterShutdown = 6;

    @Test
    public void test() {
        List<Integer> list = new ArrayList<Integer>();
        MockApsServer server = new MockApsServer(list);
        MockMessageHandler handler = new MockMessageHandler();
        server.setMessageHandler(handler);
        server.addServerStatusListener(new MockListener(list));
        server.start();
        assertTrue(handler.isInit);
        assertEquals(Arrays.asList(0, 1, 2, 3), list);
        server.stop();
        assertTrue(handler.isDestory);
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6), list);
    }

    private class MockMessageHandler implements MessageHandler {
        boolean isInit;
        boolean isDestory;

        @Override
        public void handlerMessage(MessageChannel channel) {

        }

        @Override
        public void init(ApsContext context) {
            isInit = true;
        }

        @Override
        public void destroy() {
            isDestory = true;
        }

    }

    private class MockListener implements ApsServerStatusListener {
        private final List<Integer> array;

        private MockListener(List<Integer> array) {
            super();
            this.array = array;
        }

        @Override
        public void beforeStart(ApsContext context) {
            array.add(beforeStart);
        }

        @Override
        public void afterStart(ApsContext context) {
            array.add(afterStart);

        }

        @Override
        public void beforeStop(ApsContext context) {
            array.add(beforeShutdown);
        }

        @Override
        public void afterStop(ApsContext context) {
            array.add(afterShutdown);
        }

    }

    public class MockApsServer extends ApsServer {
        private final List<Integer> array;

        private MockApsServer(List<Integer> array) {
            super();
            this.array = array;
        }

        @Override
        protected void destroy() {

        }
        @Override
        protected void initialize(ApsContext context,MessageHandler messageHandler) {
            array.add(init);
        }

        @Override
        protected void doStart() {
            array.add(doStart);
        }

        @Override
        protected void doStop() {
            array.add(doShutdown);
        }

    }
}
