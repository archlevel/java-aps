package com.anjuke.test.child;

import org.junit.Test;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.anjuke.test.child.another.ChildBeanInject;
import com.anjuke.test.child.another.ChildBeanXmlConf;

public class ChildContextInitTest {
    @Test
    public void test() {
        BeanFactoryReference ref = ContextSingletonBeanFactoryLocator
                .getInstance().useBeanFactory("parentContext");
        ApplicationContext parent = (ApplicationContext) ref.getFactory();

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "classpath:applicationContext.xml" }, parent);
        context.getBean(ChildBeanInject.class);
        context.getBean(ChildBeanXmlConf.class);
        context.close();
        ref.release();

    }

}
