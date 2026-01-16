package com.example.difyintegration.framework.config;

import com.example.difyintegration.framework.DriverFramework;
import com.example.difyintegration.framework.discovery.ServiceDiscovery;
import com.example.difyintegration.framework.executor.MethodExecutor;
import com.example.difyintegration.framework.formatter.ResponseFormatter;
import com.example.difyintegration.framework.parser.RequestParser;
import com.example.difyintegration.framework.registry.ServiceRegistry;
import com.example.difyintegration.service.AIServiceExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * 驱动框架配置类
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DriverFrameworkConfig {

    private final ServiceDiscovery serviceDiscovery;
    private final ServiceRegistry serviceRegistry;
    private final AIServiceExecutor aiServiceExecutor;

    @Bean
    public ResponseFormatter responseFormatter(ObjectMapper objectMapper) {
        return new ResponseFormatter(objectMapper);
    }

    @Bean
    public DriverFramework driverFramework() {
        log.info("Initializing Driver Framework...");
        DriverFramework framework = new DriverFramework(
            serviceRegistry.getAIServiceRegistry(),
            aiServiceExecutor
        );
        framework.initialize();
        return framework;
    }

    /**
     * 初始化服务发现
     */
    @PostConstruct
    public void initializeServiceDiscovery() {
        // 扫描服务
        serviceDiscovery.scanServices("com.example.difyintegration");
        log.info("Service discovery initialized");
    }
}