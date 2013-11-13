package com.anjuke.aps.message.protocol;

import com.google.common.collect.Multimap;

public interface Response {

    String getVersion();

    long getSequence();

    /**
     * millisecond
     *
     * @return
     */
    long getResponseTimestamp();

    /**
     * millisecond
     *
     * @param responseTimestamp
     */
    void setResponseTimestamp(long responseTimestamp);

    int getStatus();

    void setStatus(int status);

    Object getResult();

    /**
     * Only allow number, string, map, collection type
     *
     * @param result
     */
    void setResult(Object result);

    String getErrorMessage();

    void setErrorMessage(String message);

    Multimap<String, Object> getExtraMap();

    void setExtra(String key, Object value);

}
