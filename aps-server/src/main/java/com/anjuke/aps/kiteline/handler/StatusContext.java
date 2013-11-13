package com.anjuke.aps.kiteline.handler;

import java.util.concurrent.atomic.AtomicLong;

public class StatusContext {

    private final long startTime=System.currentTimeMillis();

    private final AtomicLong requests=new AtomicLong();

    private final AtomicLong pendings=new AtomicLong();

    private final AtomicLong exceptions=new AtomicLong();

    public long getUpTime() {
        return (System.currentTimeMillis()-startTime)/1000;
    }

    public void incRequests(){
        requests.incrementAndGet();
    }

    public long getRequests() {
        return requests.get();
    }

    public void incPendings(){
        pendings.incrementAndGet();

    }

    public long getPendings() {
        return pendings.get();
    }

    public void incExceptions(){
        exceptions.incrementAndGet();
    }

    public long getExceptions() {
        return exceptions.get();
    }



}
