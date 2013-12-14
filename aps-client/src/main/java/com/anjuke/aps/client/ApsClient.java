package com.anjuke.aps.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public interface ApsClient {

    /**
     *
     * @param url
     * @param timeoutMilliseconds
     * @param args 只接收 number, string, map, collection type
     * @return request result
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ExecutionException
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
