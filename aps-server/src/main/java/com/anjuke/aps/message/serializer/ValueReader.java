package com.anjuke.aps.message.serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.msgpack.type.ValueType;
import org.msgpack.unpacker.Unpacker;

import com.anjuke.aps.exception.ApsException;

class ValueReader {
    private static final List<ValueUnpacker> UNPAKER_LIST = Arrays.asList(
            new NullUnpacker(),
            new BooleanUnpacker(),
            new NumberUnpacker(),
            new FloatUnpacker(),
            new ArrayUnpacker(),
            new MapUnpacker(),
            new RawUnpacker()
    );

    private final Unpacker unpacker;

    ValueReader(Unpacker unpacker) {
        this.unpacker = unpacker;
    }

    Object readValue() {
        try {
            ValueType type = unpacker.getNextType();

            for (ValueUnpacker valueUnpacker : UNPAKER_LIST) {
                if (valueUnpacker.accept(type)) {
                    return valueUnpacker.readValue(unpacker, this);
                }
            }
            //this may hardly happen
            throw new ApsException("unkown encode type, " + type.toString());
        } catch (IOException e) {
            throw new ApsException(e);
        }
    }
}
