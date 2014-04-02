package com.anjuke.test.child;

import javax.annotation.PostConstruct;

import com.anjuke.aps.ExceptionHandler;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;

public class TestExceptionHandler implements ExceptionHandler{


    @Override
    public void handleException(Exception e, Request request, Response response) {
        response.setErrorMessage("TestExceptionHandler");
        response.setStatus(9999);
        response.setResult(Integer.MAX_VALUE);

    }


}
