package com.anjuke.test.parent;

import org.junit.Test;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

public class ParentContextInitTest {
    @Test
    public void test(){
            BeanFactoryReference ref = ContextSingletonBeanFactoryLocator
                    .getInstance().useBeanFactory("parentContext");
            ref.release();

    }

}
