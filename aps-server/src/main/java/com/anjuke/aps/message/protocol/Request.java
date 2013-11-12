package com.anjuke.aps.message.protocol;

import java.util.List;

import com.google.common.collect.Multimap;

public interface Request {

    String getVersion();

    long getSequence();

    double getRequestTimestamp();

    int getExpiry();

    String getRequestMethod();

    List<Object> getRequestParams();

    Object getExtra(String key);

    boolean hasExtra(String key);

    Multimap<String, Object> getExtraMap();

}
