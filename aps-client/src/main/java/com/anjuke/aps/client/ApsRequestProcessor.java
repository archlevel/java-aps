package com.anjuke.aps.client;

import java.util.concurrent.Future;

import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.protocol.Protocol;

public interface ApsRequestProcessor {
    Future<Response> request(Request request,Protocol protocol);
}
