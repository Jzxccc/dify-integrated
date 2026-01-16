package com.example.difyintegration.framework.executor;

import com.example.difyintegration.framework.parser.RequestParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 方法执行器，负责执行服务方法调用
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MethodExecutor {

    /**
     * 执行方法调用
     */
    public Object executeMethod(Object serviceBean, Method method, Object... args) {
        try {
            log.debug("Executing method: {}.{} with arguments: {}", 
                      serviceBean.getClass().getSimpleName(), method.getName(), args);

            // 确保方法是可访问的
            method.setAccessible(true);

            // 执行方法
            Object result = method.invoke(serviceBean, args);

            log.debug("Method execution completed successfully");

            return result;
        } catch (IllegalAccessException e) {
            log.error("Illegal access to method: " + method.getName(), e);
            throw new RuntimeException("Access error while executing service: " + method.getName(), e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            log.error("Error executing method: " + method.getName(), e.getTargetException());
            throw new RuntimeException("Execution error in service: " + method.getName(), e.getTargetException());
        }
    }
}