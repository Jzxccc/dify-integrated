package com.example.difyintegration.util;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON Schema生成器，用于生成AI可理解的服务描述
 */
@Component
@RequiredArgsConstructor
public class AIServiceSchemaGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 为带AIService注解的方法生成JSON Schema
     *
     * @param method 带AIService注解的方法
     * @return JSON Schema字符串
     */
    public String generateSchema(Method method) {
        AIService aiService = method.getAnnotation(AIService.class);
        
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", aiService.name().isEmpty() ? method.getName() : aiService.name());
        schema.put("description", aiService.description());
        schema.put("requires_auth", aiService.requiresAuth());
        
        // 生成参数Schema
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        for (Parameter param : method.getParameters()) {
            AIParam aiParam = param.getAnnotation(AIParam.class);
            if (aiParam != null) {
                Map<String, Object> paramDef = new HashMap<>();
                paramDef.put("type", aiParam.type());
                paramDef.put("description", aiParam.description());
                
                if (!aiParam.example().isEmpty()) {
                    paramDef.put("example", aiParam.example());
                }
                
                properties.put(
                    aiParam.name().isEmpty() ? param.getName() : aiParam.name(), 
                    paramDef
                );
            }
        }
        
        parameters.put("properties", properties);
        
        // 生成必需参数列表
        Map<String, Boolean> requiredParams = new HashMap<>();
        for (Parameter param : method.getParameters()) {
            AIParam aiParam = param.getAnnotation(AIParam.class);
            if (aiParam != null && aiParam.required()) {
                requiredParams.put(
                    aiParam.name().isEmpty() ? param.getName() : aiParam.name(), 
                    true
                );
            }
        }
        
        if (!requiredParams.isEmpty()) {
            parameters.put("required", requiredParams.keySet());
        }
        
        schema.put("parameters", parameters);
        
        try {
            return objectMapper.writeValueAsString(schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to generate schema for method: " + method.getName(), e);
        }
    }
}