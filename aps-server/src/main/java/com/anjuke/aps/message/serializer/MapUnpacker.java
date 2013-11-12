package com.anjuke.aps.message.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

class MapUnpacker implements ValueUnpacker {
    @Override
    public boolean accept(ValueType type) {
        return type == ValueType.MAP;
    }

    @Override
    public Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException {
        int size = unpacker.readMapBegin();
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (int i = 0; i < size; i++) {
            Object key = valueReader.readValue();
            Object value = valueReader.readValue();
            map.put(key, value);
        }
        unpacker.readMapEnd();

        return map;
    }
}
