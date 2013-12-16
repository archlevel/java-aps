package com.anjuke.test.child.another;

import org.springframework.beans.factory.InitializingBean;

import com.anjuke.aps.test.parent.ParentBean;

public class ChildBeanXmlConf implements InitializingBean{
    private ParentBean parent;

    public ParentBean getParent() {
        return parent;
    }

    public void setParent(ParentBean parent) {
        this.parent = parent;
    }


    public void afterPropertiesSet() throws Exception {
        System.out.println("================xml: "+parent);

    }

    public String echo(String msg){
        return msg;
    }

    public ParentBean parentBean(){
        return parent;
    }

}
