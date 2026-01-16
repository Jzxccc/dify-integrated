package com.example.difyintegration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记可由AI调用的服务方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AIService {
    /**
     * 服务的名称
     */
    String name() default "";
    
    /**
     * 服务的描述
     */
    String description() default "";
    
    /**
     * 是否需要认证
     */
    boolean requiresAuth() default false;
}