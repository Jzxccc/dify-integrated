package com.example.difyintegration.framework.registry;

import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 服务注册表，统一管理服务的注册和查询
 */
@Component
@RequiredArgsConstructor
public class ServiceRegistry {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 获取底层AI服务注册表
     */
    public AIServiceRegistry getAIServiceRegistry() {
        return aiServiceRegistry;
    }
}