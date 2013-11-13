package com.anjuke.aps.message.serializer;

import java.io.IOException;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

interface ValueUnpacker {

    boolean accept(ValueType type);

    Object readValue(Unpacker unpacker, ValueReader valueReader)
            throws IOException;
}
