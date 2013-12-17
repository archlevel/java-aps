package com.anjuke.aps.server;

import com.anjuke.aps.ApsContext;

public interface ApsServerStatusListener {

    void beforeStart(ApsContext context);

    void afterStart(ApsContext context);

    void beforeStop(ApsContext context);

    void afterStop(ApsContext context);
}
