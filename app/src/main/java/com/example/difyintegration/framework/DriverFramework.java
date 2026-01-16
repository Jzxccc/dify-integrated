package com.example.difyintegration.framework;

import com.example.difyintegration.service.AIServiceRegistry;
import com.example.difyintegration.service.AIServiceExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 驱动框架主类，协调各组件工作
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DriverFramework {

    private final AIServiceRegistry serviceRegistry;
    private final AIServiceExecutor serviceExecutor;

    /**
     * 初始化框架
     */
    public void initialize() {
        log.info("Initializing Driver Framework...");
        log.info("Driver Framework initialized successfully with {} services registered",
                 serviceRegistry.getAllServiceNames().size());
    }

    /**
     * 执行服务调用
     */
    public Object executeServiceCall(String serviceName, java.util.Map<String, Object> parameters) {
        log.debug("Executing service call: {} with parameters: {}", serviceName, parameters);
        return serviceExecutor.executeService(serviceName, parameters);
    }

    /**
     * 获取服务注册表
     */
    public AIServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    /**
     * 获取服务执行器
     */
    public AIServiceExecutor getServiceExecutor() {
        return serviceExecutor;
    }
}