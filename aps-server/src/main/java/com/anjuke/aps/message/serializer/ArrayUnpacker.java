package com.anjuke.aps.message.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

class ArrayUnpacker implements ValueUnpacker {

    @Override
    public boolean accept(ValueType type) {
        return type == ValueType.ARRAY;
    }

    @Override
    public Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException {
        int size = unpacker.readArrayBegin();
        List<Object> list = new ArrayList<Object>(size);
        for (int i = 0; i < size; i++) {
            list.add(valueReader.readValue());
        }
        unpacker.readArrayEnd(true);
        return list;
    }
}
