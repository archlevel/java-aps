package com.anjuke.aps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApsContext {

    public static String LOAD_MODULE_KEY="aps.server.load.module";

    public static String SERVER_ZMQ_ENDPOINT_KEY="aps.server.zmq.entpoint";

    private Map<String, Object> context = new ConcurrentHashMap<String, Object>();

    public void setAttribute(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) context.get(key);
    }
}
