package com.anjuke.aps.message.serializer;

import java.io.IOException;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

class BooleanUnpacker implements ValueUnpacker {

    @Override
    public boolean accept(ValueType type) {
        return type == ValueType.BOOLEAN;
    }

    @Override
    public Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException {
        return unpacker.readBoolean();
    }
}
