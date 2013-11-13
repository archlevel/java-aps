package com.anjuke.aps.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Asserts {

    private static final boolean checkType;

    static {
        String checkProp = System.getProperty("aps.message.type.check", "true");
        boolean check;
        try {
            check = Boolean.parseBoolean(checkProp);
        } catch (Exception e) {
            check = true;
        }
        checkType = check;
    }

    private Asserts() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }

    @SuppressWarnings("unchecked")
    public static void allowedType(Object obj) {

        if (!checkType) {
            return;
        }

        if (obj == null) {
            return;
        }
        if (obj instanceof String) {
            return;
        }

        if (obj instanceof Number) {
            return;
        }

        if (obj instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) obj;
            for (Entry<Object, Object> entry : map.entrySet()) {
                allowedType(entry.getKey());
                allowedType(entry.getValue());
            }
            return;
        }

        if (obj instanceof Collection) {
            Collection<Object> col = (Collection<Object>) obj;
            for (Object o : col) {
                allowedType(o);
            }
            return;
        }

        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            for (Object o : array) {
                allowedType(o);
            }
            return;
        }

        throw new IllegalStateException(
                "Only allow number, string, map, collection type");
    }
}
