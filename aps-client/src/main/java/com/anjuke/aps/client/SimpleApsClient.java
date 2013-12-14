package com.anjuke.aps.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import com.anjuke.aps.ExtraMessage;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.protocol.Protocol;
import com.anjuke.aps.util.ApsUtils;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;

public class SimpleApsClient implements ApsClient {

    private static final String CLIENT_ID = ApsUtils.pid() + "@"
            + ApsUtils.hostname();
    private final Protocol protocol;

    private final ApsRequestProcessor processor;
    private final AtomicLong sequence = new AtomicLong();

    public SimpleApsClient(Protocol protocol, ApsRequestProcessor processor) {
        this.protocol = protocol;
        this.processor = processor;
    }

    @Override
    public Future<Object> asyncRequest(String url, int timeoutMilliseconds,
            Object... args) {
        Request request = protocol.prepareRequest(sequence.incrementAndGet(),
                url, timeoutMilliseconds, args);
        request.setExtra(ExtraMessage.SENDER, CLIENT_ID);
        Future<Response> responseFuture = processor.request(request, protocol);

        return Futures.lazyTransform(responseFuture,
                new Function<Response, Object>() {
                    public Object apply(Response response) {
                        return response.getResult();
                    }
                });
    }

    @Override
    public Object request(String url, int timeoutMilliseconds, Object... args)
            throws TimeoutException, InterruptedException, ExecutionException {
        return asyncRequest(url, timeoutMilliseconds, args).get();
    }

}