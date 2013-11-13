package com.anjuke.aps.message.serializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;
import org.msgpack.unpacker.Unpacker;

import com.anjuke.aps.message.serializer.ValueReader;

public class ValueReaderTest {

    private MessagePack pack = new MessagePack();

    private BufferPacker packer;

    private ValueReader createReader() throws IOException {
        byte[] data = packer.toByteArray();
        packer.close();
        packer = pack.createBufferPacker();
        Unpacker unpacker = pack.createBufferUnpacker(data);
        return new ValueReader(unpacker);
    }

    @Before
    public void setUp() throws Exception {
        packer = pack.createBufferPacker();
    }

    @After
    public void tearDown() throws Exception {
        packer.close();
    }

    @Test
    public void testNull() throws IOException {
        packer.writeNil();
        ValueReader reader = createReader();
        assertNull(reader.readValue());
    }

    @Test
    public void testNumber() throws IOException {
        packer.write(123);
        ValueReader reader = createReader();
        assertEquals(123, reader.readValue());

        packer.write(1212312311111111111l);
        reader = createReader();
        assertEquals(1212312311111111111l, reader.readValue());

        packer.write(-1212312311111111111l);
        reader = createReader();
        assertEquals(-1212312311111111111l, reader.readValue());

        packer.write(-123);
        reader = createReader();
        assertEquals(-123, reader.readValue());
    }

    @Test
    public void testFloat() throws IOException {
        packer.write(12.3);
        ValueReader reader = createReader();
        assertEquals(12.3, reader.readValue());

        packer.write(12123123111.11111111);
        reader = createReader();
        assertEquals(12123123111.11111111, reader.readValue());

        packer.write(-12123123111.11111111);
        reader = createReader();
        assertEquals(-12123123111.11111111, reader.readValue());

        packer.write(-12.3);
        reader = createReader();
        assertEquals(-12.3, reader.readValue());
    }

    @Test
    public void testBoolean() throws IOException {
        packer.write(true);
        ValueReader reader = createReader();
        assertTrue((Boolean) reader.readValue());

        packer.write(false);
        reader = createReader();
        assertFalse((Boolean) reader.readValue());
    }

    @Test
    public void testRaw() throws IOException {
        packer.write("abcde");
        ValueReader reader = createReader();
        assertEquals("abcde", reader.readValue());
    }

    @Test
    public void testArray() throws IOException {
        ValueReader reader;
        List<? extends Object> expect = Arrays.<Object>asList(1, "2", 3.0);
        packer.write(expect);
        reader = createReader();
        assertEquals(expect, reader.readValue());

        expect = Arrays.asList(1, "2", 3.0, Arrays.<Object>asList(1, 2.0, "3"),
                asMap("abc", 123));
        packer.write(expect);
        reader = createReader();
        assertEquals(expect, reader.readValue());
    }

    @Test
    public void testMap() throws IOException {
        ValueReader reader;
        Object expect = asMap("1", Arrays.<Object>asList("1,2,3", 1, 2, 3));
        packer.write(expect);
        reader = createReader();
        assertEquals(expect, reader.readValue());

        expect = asMap(
                "1",
                Arrays.<Object>asList(1, "2", 3.0, Arrays.<Object>asList(1, 2.0, "3"),
                        asMap("abc", 123)));
        packer.write(expect);
        reader = createReader();
        assertEquals(expect, reader.readValue());

        expect = asMap(
                "a",
                asMap("b",
                        asMap("c",
                                asMap("d",
                                        asMap("e", 123, "array",
                                                Arrays.asList(1))))));
        packer.write(expect);
        reader = createReader();
        assertEquals(expect, reader.readValue());
    }

    private Map<Object, Object> asMap(Object... args) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        for (int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i + 1]);
        }
        return map;
    }
}
