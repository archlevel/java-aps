package com.anjuke.aps.message.protocol;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.anjuke.aps.exception.UnknownProtocolException;
import com.anjuke.aps.message.serializer.Serializer;

public class ProtocolFactory {

    static final String APS_10_VERSION = "APS10";
    static final String APS_12_VERSION = "APS12";

    private final static Map<String, Protocol> protocolMapping;

    static {
        protocolMapping = new HashMap<String, Protocol>(2);
        protocolMapping.put(APS_10_VERSION, new APS10Protocol());
        protocolMapping.put(APS_12_VERSION, new APS12Protocol());
    }

    public static Protocol parseProtocol(Deque<byte[]> frames,
            Serializer serializer) {
        byte[] versionFrame = frames.pollFirst();
        String version = new String(versionFrame);
        Protocol protocol = protocolMapping.get(version);
        if (protocol == null) {
            throw new UnknownProtocolException("Unknown APS Version " + version);
        } else {
            return protocol;
        }
    }
}
