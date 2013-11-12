package com.anjuke.aps.message.serializer;

import java.io.IOException;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

class RawUnpacker implements ValueUnpacker {
    @Override
    public boolean accept(ValueType type) {

        return type == ValueType.RAW;
    }

    @Override
    public Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException {
        byte[] data = unpacker.readByteArray();
        return new String(data);
    }

}
