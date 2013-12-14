package com.anjuke.aps;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SimpleResponse implements Response {
    private String version = "TEST";
    private long sequence;
    private long responseTimestamp;
    private int status = 200;
    private Object result;

    private Multimap<String, Object> extraMap = ArrayListMultimap.create();

    public long getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getErrorMessage() {
        Collection<Object> msg = extraMap.get(ExtraMessage.ERROR_MESSAGE);
        if (msg == null || msg.isEmpty()) {
            return null;
        } else {
            return Arrays.toString(msg.toArray());
        }
    }

    @Override
    public void setErrorMessage(String message) {
        this.extraMap.put(ExtraMessage.ERROR_MESSAGE, message);
    }

    @Override
    public Multimap<String, Object> getExtraMap() {
        return extraMap;
    }

    @Override
    public void setExtra(String key, Object value) {
        extraMap.put(key, value);
    }

}
