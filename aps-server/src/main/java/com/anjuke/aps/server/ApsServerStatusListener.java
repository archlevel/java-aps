package com.anjuke.aps.server;

public interface ApsServerStatusListener {

    void beforeStart();

    void afterStart();

    void beforeStop();

    void afterStop();
}
