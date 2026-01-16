package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 服务发现API，让agent了解可用服务
 */
@Service
@RequiredArgsConstructor
public class AIServiceDiscovery {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 获取所有可用服务的名称
     */
    @AIService(
        name = "get_available_services",
        description = "获取所有可用服务的名称列表",
        requiresAuth = true
    )
    public Set<String> getAvailableServiceNames() {
        return aiServiceRegistry.getAllServiceNames();
    }

    /**
     * 获取特定服务的Schema
     */
    @AIService(
        name = "get_service_schema",
        description = "获取特定服务的Schema定义",
        requiresAuth = true
    )
    public String getServiceSchema(String serviceName) {
        var serviceInfo = aiServiceRegistry.getService(serviceName);
        if (serviceInfo.isPresent()) {
            return serviceInfo.get().getSchema();
        } else {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }
    }
}