package com.anjuke.aps;

public interface ApsStatus {
    int SUCCESS = 200;
    int CREATED = 201;
    int ACCEPTED = 202;
    int NO_CONTENT = 204;

    int NOT_MODIFIED = 304;

    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int METHOD_NOT_FOUND = 404;
    int TOO_MANY_REQUESTS = 429;

    int INTENAL_SERVER_ERROR = 500;
    int NOT_IMPLEMENTED = 501;
    int BAD_GATEWAY = 502;
    int SERVICE_UNAVAILABLE = 503;
    int GATEWAY_TIMEOUT = 504;
    int BANDWIDTH_LIMIT_EXCEEDED = 509;
}
