package com.anjuke.aps.spring.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ServiceInstanceBeanDefinitionParser implements
        BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String className = element.getAttribute("class");
        BeanDefinition definition= BeanDefinitionBuilder.genericBeanDefinition(ApsServiceInstance.class)
                .addPropertyValue("serviceClass", className).getBeanDefinition();
        parserContext.registerBeanComponent(new BeanComponentDefinition(definition,className));
        return null;
    }

}
