package com.anjuke.aps.message.serializer;

public interface Serializer {

    public abstract String readString(byte[] data);

    public abstract <T> T readValue(byte[] data);

    /**
     * write value to byte array
     *
     * obj的类型只能是java.lang.Number, java.lang.String, java.util.Collection,
     * java.util.Map
     *
     * 包括Collection以及Map内的元素也只能是这几个类型
     *
     * @param obj
     * @return
     */
    public abstract byte[] writeValue(Object obj);

    public abstract byte[] writeString(String str);

}