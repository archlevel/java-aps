package com.anjuke.aps;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Multimap;

public interface Request {

    String getVersion();

    long getSequence();

    /**
     * millisecond
     * @return
     */
    long getRequestTimestamp();

    /**
     *
     * @return millisecond
     */
    int getExpiry();

    String getRequestMethod();

    List<Object> getRequestParams();

    void setExtra(String key, Object value);

    Collection<Object> getExtra(String key);

    boolean hasExtra(String key);

    Multimap<String, Object> getExtraMap();


}
