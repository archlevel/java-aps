package com.anjuke.aps.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApsMethod {

    /**
     * Spring里的beanName
     */
    String bean();

    /**
     * 对外服务的方法名
     */
    String method();

    /**
     * 实际调用的bean的方法名，默认和注解标记的方法名相同
     */
    String targetMethodName() default "";
}
