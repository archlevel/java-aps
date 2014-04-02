package com.anjuke.aps.server.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.ExceptionHandler;

public class DefaultExceptionHandler implements ExceptionHandler{

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultExceptionHandler.class);

    @Override
    public void handleException(Exception e, Request request, Response response) {
         LOG.error(e.getMessage(), e);
         response.setErrorMessage(e.getMessage());
         response.setStatus(ApsStatus.INTENAL_SERVER_ERROR);
    }

}
