package com.anjuke.aps.message.serializer;

import java.io.IOException;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

class NumberUnpacker implements ValueUnpacker {
    @Override
    public boolean accept(ValueType type) {
        return type == ValueType.INTEGER;
    }

    @Override
    public Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException {
        long value = unpacker.readLong();
        if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
            return (int) value;
        }
        return value;
    }
}
