package com.anjuke.test.child;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;

import com.anjuke.aps.test.parent.ParentBean;

public class ChildBeanInject implements InitializingBean{
    @Resource
    private ParentBean parent;

    public void afterPropertiesSet() throws Exception {
        System.out.println("==================inject: "+parent);

    }

    public String echo(String msg){
        return msg;
    }

    public ParentBean parentBean(){
        return parent;
    }
}
