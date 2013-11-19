package com.anjuke.aps.server.spring;

import com.anjuke.aps.ApsConfig;
import com.anjuke.aps.zmq.ApsZMQServerMain;

public class SpringServer {
    public static void main(String[] args) throws InterruptedException {

        System.setProperty(ApsConfig.CONFIG_PATH_KEY,"file://"+System.getProperty("user.dir")+"/src/main/resources/aps_spring.yaml");
        ApsZMQServerMain.main(args);
    }
}
