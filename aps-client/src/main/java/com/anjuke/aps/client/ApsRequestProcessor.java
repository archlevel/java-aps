package com.anjuke.aps.client;

import java.util.concurrent.Future;

import com.anjuke.aps.message.protocol.Protocol;
import com.anjuke.aps.message.protocol.Request;
import com.anjuke.aps.message.protocol.Response;

public interface ApsRequestProcessor {
    Future<Response> request(Request request,Protocol protocol);
}
