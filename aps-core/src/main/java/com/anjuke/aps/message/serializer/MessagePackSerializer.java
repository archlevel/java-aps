package com.anjuke.aps.message.serializer;

import java.io.IOException;

import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;

import com.anjuke.aps.exception.ApsException;

public class MessagePackSerializer implements Serializer {

    private final MessagePack msgpack = new MessagePack();

    /* (non-Javadoc)
     * @see com.anjuke.aps.server.message.protocol.Protocol#readString(byte[])
     */
    @Override
    public String readString(byte[] data) {
        return new String(data);
    }

    /* (non-Javadoc)
     * @see com.anjuke.aps.server.message.protocol.Protocol#readValue(byte[])
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T readValue(byte[] data) {
        Unpacker unpacker = msgpack.createBufferUnpacker(data);
        return (T) new ValueReader(unpacker).readValue();
    }

    /* (non-Javadoc)
     * @see com.anjuke.aps.server.message.protocol.Protocol#writeValue(java.lang.Object)
     */
    @Override
    public byte[] writeValue(Object obj) {
        BufferPacker packer = msgpack.createBufferPacker();
        try {
            packer.write(obj);
            return packer.toByteArray();
        } catch (IOException e) {
            throw new ApsException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.anjuke.aps.server.message.protocol.Protocol#writeString(java.lang.String)
     */
    @Override
    public byte[] writeString(String str) {
        return str.getBytes();
    }

}
