package com.anjuke.aps.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApsRequest {
    String url();
    int timeout() default 0;
    boolean async() default false;
    Class<? extends ApsAsyncCallback<?>> asyncCallback() default DefaultApsAsyncCallback.class;
}
