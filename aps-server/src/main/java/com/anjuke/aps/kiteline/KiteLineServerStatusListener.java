package com.anjuke.aps.kiteline;

import java.util.Set;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.client.DefaultClientRequestProcessor;
import com.anjuke.aps.client.SimpleApsClient;
import com.anjuke.aps.message.protocol.ProtocolFactory;
import com.anjuke.aps.message.serializer.MessagePackSerializer;
import com.anjuke.aps.server.ApsServerStatusListener;

public class KiteLineServerStatusListener implements ApsServerStatusListener {

    private DefaultClientRequestProcessor requestProcessor;
    private SimpleApsClient client;

    private String identity;

    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void beforeStart(ApsContext context) {
        requestProcessor = new DefaultClientRequestProcessor(
                new MessagePackSerializer(), endpoint);
        requestProcessor.init(context);
        client = new SimpleApsClient(
                ProtocolFactory.getProtocol(ProtocolFactory.APS_12_VERSION),
                requestProcessor);
        identity = context.getAttribute(ApsContext.SERVER_ZMQ_ENDPOINT_KEY);
        identity = "tcp://" + "192.168.196.119" + ":" + 8964;
    }

    @Override
    public void afterStart(ApsContext context) {
        Set<String> modules = context.getAttribute(ApsContext.LOAD_MODULE_KEY);
        try {
            for (String module : modules) {
                Object result=client.request("sp.up", 1000, module, identity, "20134901");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeStop(ApsContext context) {
        try {
            client.request("sp.down", 1000, identity);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void afterStop(ApsContext context) {
        requestProcessor.destroy();

    }

}
