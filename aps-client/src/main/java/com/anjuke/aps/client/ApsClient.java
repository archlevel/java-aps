package com.anjuke.aps.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface ApsClient {

    /**
     *
     * @param url
     * @param timeoutMilliseconds
     * @param args 只接收 number, string, map, collection type
     * @return
     * @throws TimeoutException
     * @throws InterruptedException
     */
    public Object request(String url, int timeoutMilliseconds, Object... args)
            throws TimeoutException, InterruptedException,ExecutionException;
    /**
     *
     * @param url
     * @param timeoutMilliseconds
     * @param args 只接收 number, string, map, collection type
     * @return
     */
    public Future<Object> asyncRequest(String url, int timeoutMilliseconds,
            Object... args);
}
