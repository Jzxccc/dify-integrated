package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务标签系统
 */
@Service
@RequiredArgsConstructor
public class AIServiceTagSystem {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 获取所有服务的标签
     */
    @AIService(
        name = "get_service_tags",
        description = "获取所有服务的标签",
        requiresAuth = true
    )
    public Map<String, List<String>> getServiceTags() {
        Map<String, List<String>> serviceTags = new HashMap<>();
        
        for (AIServiceRegistry.ServiceInfo serviceInfo : aiServiceRegistry.getAllServices()) {
            String serviceName = serviceInfo.getAIServiceAnnotation().name().isEmpty() 
                ? serviceInfo.getMethod().getName() 
                : serviceInfo.getAIServiceAnnotation().name();
            
            // 根据服务分类自动生成标签
            List<String> tags = new ArrayList<>();
            tags.add(serviceInfo.getCategory().toLowerCase().replace(" ", "_")); // 分类作为标签
            
            // 根据服务名称生成标签
            if (serviceName.toLowerCase().contains("user")) {
                tags.add("user");
            }
            if (serviceName.toLowerCase().contains("conversation")) {
                tags.add("conversation");
            }
            if (serviceName.toLowerCase().contains("message")) {
                tags.add("message");
            }
            if (serviceName.toLowerCase().contains("auth")) {
                tags.add("authentication");
            }
            
            serviceTags.put(serviceName, tags);
        }
        
        return serviceTags;
    }

    /**
     * 根据标签搜索服务
     */
    @AIService(
        name = "search_services_by_tag",
        description = "根据标签搜索服务",
        requiresAuth = true
    )
    public List<String> searchServicesByTag(
        @AIParam(name = "tag", description = "标签", type = "string", required = true)
        String tag) {
        
        Map<String, List<String>> allTags = getServiceTags();
        return allTags.entrySet().stream()
                .filter(entry -> entry.getValue().contains(tag.toLowerCase()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}