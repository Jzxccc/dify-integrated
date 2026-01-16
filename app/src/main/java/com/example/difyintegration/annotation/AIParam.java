package com.example.difyintegration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记AI服务方法的参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AIParam {
    /**
     * 参数名称
     */
    String name() default "";
    
    /**
     * 参数描述
     */
    String description() default "";
    
    /**
     * 参数类型（用于生成Schema）
     */
    String type() default "string";
    
    /**
     * 是否必需
     */
    boolean required() default true;
    
    /**
     * 参数示例值
     */
    String example() default "";
}