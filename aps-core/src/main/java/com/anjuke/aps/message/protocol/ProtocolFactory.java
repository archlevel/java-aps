package com.anjuke.aps.message.protocol;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.anjuke.aps.exception.UnknownProtocolException;
import com.anjuke.aps.message.serializer.Serializer;

public class ProtocolFactory {

    public static final String APS_10_VERSION = "APS10";
    public static final String APS_12_VERSION = "APS1.2";

    private final static Map<String, Protocol> protocolMapping;

    static {
        protocolMapping = new HashMap<String, Protocol>(2);
        protocolMapping.put(APS_10_VERSION, new APS10Protocol());
        protocolMapping.put(APS_12_VERSION, new APS12Protocol());
    }

    // public static Protocol defaultProtocol() {
    // return protocolMapping.get(APS_12_VERSION);
    // }

    public static Protocol getProtocol(String version) {
        Protocol protocol = protocolMapping.get(version);
        if (protocol == null) {
            throw new UnknownProtocolException("Unknown APS Version " + version);
        } else {
            return protocol;
        }
    }

    public static Protocol parseProtocol(Deque<byte[]> frames,
            Serializer serializer) {
        byte[] versionFrame = frames.pollFirst();
        String version = new String(versionFrame);
        return getProtocol(version);
    }
}
