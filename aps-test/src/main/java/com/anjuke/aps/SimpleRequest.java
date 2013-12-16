package com.anjuke.aps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class SimpleRequest implements Request {
    private String version = "TEST";
    private long sequence;

    private long requestTimestamp;
    private int expiry;
    private String requestMethod = "echo";

    private List<Object> requestParams = Collections.emptyList();

    private Multimap<String, Object> extraMap = ArrayListMultimap.create();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public List<Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<Object> requestParams) {
        this.requestParams = requestParams;
    }

    @Override
    public Collection<Object> getExtra(String key) {
        return extraMap.get(key);
    }

    @Override
    public boolean hasExtra(String key) {

        return extraMap.containsKey(key);
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
