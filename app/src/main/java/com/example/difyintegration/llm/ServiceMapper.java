package com.example.difyintegration.llm;

import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 服务映射器，维护意图与服务之间的映射关系
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceMapper {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 根据用户输入和参数查找匹配的服务
     */
    public Optional<AIServiceRegistry.ServiceInfo> findMatchingService(String userInput, java.util.Map<String, Object> parameters) {
        log.debug("Finding matching service for input: {} with parameters: {}", userInput, parameters);
        
        // 获取所有可用服务
        List<AIServiceRegistry.ServiceInfo> allServices = (List<AIServiceRegistry.ServiceInfo>) aiServiceRegistry.getAllServices();
        
        // 简单的匹配算法（实际实现可能需要更复杂的匹配逻辑）
        for (AIServiceRegistry.ServiceInfo serviceInfo : allServices) {
            String serviceName = serviceInfo.getAIServiceAnnotation().name().isEmpty() 
                ? serviceInfo.getMethod().getName() 
                : serviceInfo.getAIServiceAnnotation().name();
            
            // 检查服务名称是否与用户意图匹配
            if (matchesServiceIntent(userInput, serviceName, serviceInfo)) {
                // 验证参数是否符合服务Schema
                if (validateParameters(parameters, serviceInfo)) {
                    log.info("Found matching service: {} for input: {}", serviceName, userInput);
                    return Optional.of(serviceInfo);
                }
            }
        }
        
        log.warn("No matching service found for input: {}", userInput);
        return Optional.empty();
    }

    /**
     * 检查用户输入是否与服务意图匹配
     */
    private boolean matchesServiceIntent(String userInput, String serviceName, AIServiceRegistry.ServiceInfo serviceInfo) {
        // 简单的匹配逻辑，实际实现可能需要更复杂的NLP处理
        String lowerInput = userInput.toLowerCase();
        String lowerServiceName = serviceName.toLowerCase();
        
        // 检查服务名称是否在输入中
        if (lowerInput.contains(lowerServiceName)) {
            return true;
        }
        
        // 检查服务描述是否与输入相关
        String serviceDescription = serviceInfo.getAIServiceAnnotation().description().toLowerCase();
        if (!serviceDescription.isEmpty() && lowerInput.contains(serviceDescription)) {
            return true;
        }
        
        // 检查服务分类是否与输入相关
        String category = serviceInfo.getCategory().toLowerCase();
        if (lowerInput.contains(category.replace(" ", ""))) {
            return true;
        }
        
        return false;
    }

    /**
     * 验证参数是否符合服务Schema
     */
    private boolean validateParameters(java.util.Map<String, Object> parameters, AIServiceRegistry.ServiceInfo serviceInfo) {
        // 简单的验证逻辑，实际实现可能需要更复杂的Schema验证
        // 这里我们检查必需参数是否存在
        String schema = serviceInfo.getSchema();
        log.debug("Validating parameters against schema: {}", schema);
        
        // 在实际实现中，这里应该解析服务的JSON Schema并验证参数
        // 目前返回true作为占位符
        return true;
    }
}