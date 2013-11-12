package com.anjuke.aps.message.protocol;

import com.google.common.collect.Multimap;

public interface Response {

    String getVersion();

    long getSequence();

    double getResponseTimestamp();

    void setResponseTimestamp(double responseTimestamp);

    int getStatus();

    void setStatus(int status);

    Object getResult();

    void setResult(Object result);

    String getErrorMessage();

    void setErrorMessage(String message);

    Multimap<String, Object> getExtraMap();

    void setExtra(String key, Object value);

}
