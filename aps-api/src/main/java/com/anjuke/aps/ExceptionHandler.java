package com.anjuke.aps;


public interface ExceptionHandler {

    public void handleException(Exception e, Request request, Response response);
}
