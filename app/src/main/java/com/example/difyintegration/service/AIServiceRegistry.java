package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.util.AIServiceSchemaGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 服务注册表，用于扫描、注册和管理AI服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceRegistry {

    private final AIServiceSchemaGenerator schemaGenerator;
    private final List<Object> serviceBeans = new ArrayList<>();
    private final Map<String, ServiceInfo> serviceMap = new ConcurrentHashMap<>();

    /**
     * 服务信息类
     */
    public static class ServiceInfo {
        private final Object serviceBean;
        private final Method method;
        private final String schema;
        private final AIService aiServiceAnnotation;
        private final String category; // 服务分类

        public ServiceInfo(Object serviceBean, Method method, String schema, AIService aiServiceAnnotation) {
            this.serviceBean = serviceBean;
            this.method = method;
            this.schema = schema;
            this.aiServiceAnnotation = aiServiceAnnotation;
            // 从类名推断分类
            String packageName = method.getDeclaringClass().getPackage().getName();
            if (packageName.contains(".user")) {
                this.category = "User Management";
            } else if (packageName.contains(".conversation") || packageName.contains(".chat")) {
                this.category = "Conversation Management";
            } else if (packageName.contains(".interaction") || packageName.contains(".app")) {
                this.category = "Interaction Management";
            } else if (packageName.contains(".dify") || packageName.contains(".api")) {
                this.category = "API Integration";
            } else {
                this.category = "General";
            }
        }

        public Object getServiceBean() { return serviceBean; }
        public Method getMethod() { return method; }
        public String getSchema() { return schema; }
        public AIService getAIServiceAnnotation() { return aiServiceAnnotation; }
        public String getCategory() { return category; }
    }

    /**
     * 注册服务实例
     */
    public void registerServiceBean(Object serviceBean) {
        serviceBeans.add(serviceBean);
        scanAndRegisterServices(serviceBean);
    }

    /**
     * 扫描并注册服务
     */
    private void scanAndRegisterServices(Object serviceBean) {
        Class<?> clazz = serviceBean.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(AIService.class)) {
                AIService aiService = method.getAnnotation(AIService.class);
                String serviceName = aiService.name().isEmpty() ? method.getName() : aiService.name();

                // 生成Schema
                String schema = schemaGenerator.generateSchema(method);

                // 创建服务信息
                ServiceInfo serviceInfo = new ServiceInfo(serviceBean, method, schema, aiService);

                // 注册服务
                serviceMap.put(serviceName, serviceInfo);
                log.info("Registered AI service: {} with method: {}", serviceName, method.getName());
            }
        }
    }

    /**
     * 获取服务信息
     */
    public Optional<ServiceInfo> getService(String serviceName) {
        return Optional.ofNullable(serviceMap.get(serviceName));
    }

    /**
     * 获取所有服务名称
     */
    public Set<String> getAllServiceNames() {
        return new HashSet<>(serviceMap.keySet());
    }

    /**
     * 获取所有服务信息
     */
    public Collection<ServiceInfo> getAllServices() {
        return serviceMap.values();
    }

    /**
     * 按分类获取服务
     */
    public List<ServiceInfo> getServicesByCategory(String category) {
        return serviceMap.values().stream()
                .filter(serviceInfo -> serviceInfo.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有服务分类
     */
    public Set<String> getAllCategories() {
        return serviceMap.values().stream()
                .map(ServiceInfo::getCategory)
                .collect(Collectors.toSet());  // toSet() still requires Collectors
    }

    /**
     * 初始化时扫描所有已知的服务
     */
    @PostConstruct
    public void initialize() {
        log.info("Initializing AI Service Registry with {} service beans", serviceBeans.size());
        // 对所有已注册的bean进行扫描
        for (Object bean : serviceBeans) {
            scanAndRegisterServices(bean);
        }
    }
}