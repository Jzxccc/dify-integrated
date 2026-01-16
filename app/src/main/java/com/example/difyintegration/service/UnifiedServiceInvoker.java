package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 统一服务调用入口
 */
@Service
@RequiredArgsConstructor
public class UnifiedServiceInvoker {

    private final AIServiceExecutor aiServiceExecutor;

    /**
     * 统一服务调用接口
     */
    @AIService(
        name = "invoke_service",
        description = "统一服务调用接口，根据服务名称和参数执行服务",
        requiresAuth = true
    )
    public Object invokeService(
        @AIParam(name = "serviceName", description = "服务名称", type = "string", required = true)
        String serviceName,
        @AIParam(name = "parameters", description = "服务参数", type = "object", required = false)
        Map<String, Object> parameters) {
        if (parameters == null) {
            parameters = Map.of(); // 空参数
        }
        return aiServiceExecutor.executeService(serviceName, parameters);
    }
}