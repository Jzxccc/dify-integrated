package com.example.difyintegration.config;

import com.example.difyintegration.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * AI服务注册配置
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AIServiceRegistryConfig {

    private final AIServiceRegistry aiServiceRegistry;
    private final AIServiceAdapter aiServiceAdapter;
    private final ConversationService conversationService;
    private final UserService userService;
    private final AppInteractionService appInteractionService;
    private final DifyApiClient difyApiClient;
    private final AIServiceDiscovery aiServiceDiscovery;
    private final UnifiedServiceInvoker unifiedServiceInvoker;
    private final AIServiceTagSystem aiServiceTagSystem;

    @PostConstruct
    public void registerServices() {
        log.info("Registering AI services with AI Service Registry...");
        
        // 注册所有带有AIService注解的服务
        aiServiceRegistry.registerServiceBean(aiServiceAdapter);
        aiServiceRegistry.registerServiceBean(conversationService);
        aiServiceRegistry.registerServiceBean(userService);
        aiServiceRegistry.registerServiceBean(appInteractionService);
        aiServiceRegistry.registerServiceBean(difyApiClient);
        aiServiceRegistry.registerServiceBean(aiServiceDiscovery);
        aiServiceRegistry.registerServiceBean(unifiedServiceInvoker);
        aiServiceRegistry.registerServiceBean(aiServiceTagSystem);
        
        log.info("Successfully registered {} AI services", aiServiceRegistry.getAllServiceNames().size());
    }
}