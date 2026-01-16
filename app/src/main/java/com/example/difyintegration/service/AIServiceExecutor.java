package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * AI服务执行器，用于执行AI请求的服务调用
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceExecutor {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 执行服务调用
     *
     * @param serviceName 服务名称
     * @param parameters 参数映射
     * @return 执行结果
     */
    public Object executeService(String serviceName, Map<String, Object> parameters) {
        // 获取服务信息
        var serviceInfoOpt = aiServiceRegistry.getService(serviceName);
        if (serviceInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        var serviceInfo = serviceInfoOpt.get();
        AIService annotation = serviceInfo.getAIServiceAnnotation();
        
        // 检查认证要求（这里简化处理，实际实现可能需要更复杂的认证逻辑）
        if (annotation.requiresAuth()) {
            // 在实际实现中，这里需要验证用户认证状态
            log.debug("Service {} requires authentication", serviceName);
        }

        // 准备方法参数
        Method method = serviceInfo.getMethod();
        Object[] args = prepareMethodArguments(method, parameters);

        try {
            // 执行方法
            Object result = method.invoke(serviceInfo.getServiceBean(), args);
            log.info("Successfully executed service: {}, result type: {}", serviceName, result != null ? result.getClass().getSimpleName() : "null");
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error executing service: " + serviceName, e);
            throw new RuntimeException("Error executing service: " + serviceName, e);
        }
    }

    /**
     * 准备方法参数
     */
    private Object[] prepareMethodArguments(Method method, Map<String, Object> parameters) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        var paramAnnotations = method.getParameterAnnotations();
        
        for (int i = 0; i < paramTypes.length; i++) {
            String paramName = extractParameterName(paramAnnotations[i], i);
            Object value = parameters.get(paramName);
            
            // 类型转换（简化版，实际实现可能需要更复杂的类型转换逻辑）
            args[i] = convertParameterValue(value, paramTypes[i]);
        }

        return args;
    }

    /**
     * 提取参数名称
     */
    private String extractParameterName(java.lang.annotation.Annotation[] annotations, int index) {
        for (var annotation : annotations) {
            if (annotation instanceof com.example.difyintegration.annotation.AIParam) {
                String name = ((com.example.difyintegration.annotation.AIParam) annotation).name();
                if (!name.isEmpty()) {
                    return name;
                }
            }
        }
        // 如果没有指定名称，使用参数索引作为名称（实际实现中可能需要更好的策略）
        return "param" + index;
    }

    /**
     * 转换参数值到目标类型
     */
    private Object convertParameterValue(Object value, Class<?> targetType) {
        if (value == null) {
            // 处理原始类型默认值
            if (targetType.isPrimitive()) {
                if (targetType == boolean.class) return false;
                if (targetType == char.class) return '\0';
                if (targetType == byte.class) return (byte) 0;
                if (targetType == short.class) return (short) 0;
                if (targetType == int.class) return 0;
                if (targetType == long.class) return 0L;
                if (targetType == float.class) return 0.0f;
                if (targetType == double.class) return 0.0;
            }
            return null;
        }

        // 如果类型匹配，直接返回
        if (targetType.isInstance(value) || targetType == value.getClass()) {
            return value;
        }

        // 简单的类型转换
        if (targetType == String.class) {
            return value.toString();
        } else if (targetType == Integer.class || targetType == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.valueOf(value.toString());
            }
        } else if (targetType == Long.class || targetType == long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return Long.valueOf(value.toString());
            }
        } else if (targetType == Double.class || targetType == double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                return Double.valueOf(value.toString());
            }
        } else if (targetType == Float.class || targetType == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else {
                return Float.valueOf(value.toString());
            }
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            } else {
                return Boolean.valueOf(value.toString());
            }
        }

        // 默认返回原值（可能需要更复杂的转换逻辑）
        return value;
    }
}