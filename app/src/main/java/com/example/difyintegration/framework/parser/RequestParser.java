package com.example.difyintegration.framework.parser;

import com.example.difyintegration.annotation.AIParam;
import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * 请求解析器，负责解析AI模型的请求
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestParser {

    private final AIServiceRegistry serviceRegistry;

    /**
     * 解析服务调用请求
     */
    public ParsedRequest parseRequest(String serviceName, Map<String, Object> parameters) {
        log.debug("Parsing request for service: {} with parameters: {}", serviceName, parameters);

        // 获取服务信息
        var serviceInfoOpt = serviceRegistry.getService(serviceName);
        if (serviceInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        var serviceInfo = serviceInfoOpt.get();
        Method method = serviceInfo.getMethod();

        // 验证参数并映射到方法参数
        Object[] methodArgs = mapParametersToMethodArgs(method, parameters);

        return ParsedRequest.builder()
                .serviceName(serviceName)
                .method(method)
                .serviceBean(serviceInfo.getServiceBean())
                .parameters(methodArgs)
                .rawParameters(parameters)
                .build();
    }

    /**
     * 将请求参数映射到方法参数
     */
    private Object[] mapParametersToMethodArgs(Method method, Map<String, Object> requestParams) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            AIParam aiParam = param.getAnnotation(AIParam.class);

            String paramName;
            if (aiParam != null && !aiParam.name().isEmpty()) {
                paramName = aiParam.name();
            } else {
                paramName = param.getName(); // 在运行时这通常不可靠，但在编译时保留了参数名
            }

            // 查找对应的请求参数
            Object value = requestParams.get(paramName);
            
            // 类型转换
            args[i] = convertTypeIfNeeded(value, param.getType());
        }

        return args;
    }

    /**
     * 根据需要转换参数类型
     */
    private Object convertTypeIfNeeded(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value)) {
            return value;
        }

        // 简单的类型转换
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.parseInt(value.toString());
            }
        } else if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return Long.parseLong(value.toString());
            }
        } else if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                return Double.parseDouble(value.toString());
            }
        } else if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else {
                return Float.parseFloat(value.toString());
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            } else {
                return Boolean.parseBoolean(value.toString());
            }
        }

        // 默认返回原值
        return value;
    }

    /**
     * 解析请求的结果类
     */
    @lombok.Data
    @lombok.Builder
    public static class ParsedRequest {
        private String serviceName;
        private Method method;
        private Object serviceBean;
        private Object[] parameters;
        private Map<String, Object> rawParameters;
    }
}