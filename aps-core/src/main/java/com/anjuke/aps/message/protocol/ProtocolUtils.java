package com.anjuke.aps.message.protocol;

import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ProtocolUtils {

    private static final Logger LOG = LoggerFactory
            .getLogger(ProtocolUtils.class);

    public static Deque<byte[]> appendExtraFrames(
            Multimap<String, Object> extraMap, Deque<byte[]> frames,
            Serializer serializer) {
        for (String key : extraMap.keySet()) {
            List<Object> extra = Lists.newArrayListWithCapacity(3);
            extra.add(key);
            Collection<Object> value = extraMap.get(key);
            extra.addAll(value);
            byte[] data = serializer.writeValue(extra);
            frames.offer(data);
        }
        return frames;
    }

    public static void parseExtraFrames(Multimap<String, Object> extraMap,
            Deque<byte[]> frames, Serializer serializer) {
        for (byte[] extra : frames) {
            try {
                Object extraData = serializer.readValue(extra);
                if (extraData == null) {
                    continue;
                }
                if (extraData instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Object> extraList = (List<Object>) extraData;
                    if (extraList.isEmpty()) {
                        continue;
                    }

                    String key = String.valueOf(extraList.get(0));
                    int size = extraList.size();
                    if (size == 1) {
                        Object value = null;
                        extraMap.put(key, value);
                    } else if (size == 2) {
                        Object value = extraList.get(1);
                        extraMap.put(key, value);
                    } else {
                        List<Object> value = extraList.subList(1, size);
                        extraMap.putAll(key, value);
                    }

                } else {
                    LOG.warn("Extra frame not a Array, Dropped. Value is "
                            + extraData);

                }
            } catch (Exception e) {
                LOG.warn("Unkown extra frame, dropped", e);
            }
        }
    }
}
