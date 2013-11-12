package com.anjuke.aps.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.anjuke.aps.exception.ApsException;

public abstract class ApsUtils {

    private static final int PID;
    private static final String HOSTNAME;

    static {
        PID = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName()
                .split("@")[0]);
        try {
            HOSTNAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new ApsException(e);
        }
    }

    private ApsUtils() {

    }

    public static int pid() {
        return PID;
    }

    public static String hostname() {
        return HOSTNAME;
    }

    public static ThreadFactory threadFactory(String name) {
        return new ApsThreadFactory(name);
    }

    private static class ApsThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ApsThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = name + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
    public static void main(String[] args) {
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
    }
}
