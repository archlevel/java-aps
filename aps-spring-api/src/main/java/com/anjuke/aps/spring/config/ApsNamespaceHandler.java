package com.anjuke.aps.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ApsNamespaceHandler extends NamespaceHandlerSupport{

    @Override
    public void init() {
        registerBeanDefinitionParser("service", new ServiceInstanceBeanDefinitionParser());
    }

}
