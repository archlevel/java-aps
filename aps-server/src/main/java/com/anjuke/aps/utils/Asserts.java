package com.anjuke.aps.utils;

public abstract class Asserts {

    private Asserts() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }
}
