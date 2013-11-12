package com.anjuke.aps.message;

import com.anjuke.aps.LifeCycle;


public interface MessageHandler extends LifeCycle{

    public void handlerMessage(MessageChannel channel);

}
