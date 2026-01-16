package com.example.difyintegration.framework.discovery;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 服务发现组件，负责扫描和发现被注解标记的服务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceDiscovery {

    private final AIServiceRegistry serviceRegistry;

    /**
     * 扫描指定包路径下的服务
     */
    public void scanServices(String... basePackages) {
        log.info("Starting service discovery in packages: {}", String.join(", ", basePackages));
        
        // 在实际实现中，我们会扫描指定包路径下的类
        // 但现在我们依赖于AIServiceRegistry已经注册的服务
        log.info("Service discovery completed. Services are managed by AIServiceRegistry.");
    }

    /**
     * 检查方法是否带有AIService注解
     */
    public boolean isAIServiceMethod(Method method) {
        return method.isAnnotationPresent(AIService.class);
    }

    /**
     * 获取所有已发现的服务名称
     */
    public Set<String> getDiscoveredServiceNames() {
        return new HashSet<>(serviceRegistry.getAllServiceNames());
    }
}