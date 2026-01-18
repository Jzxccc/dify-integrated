package com.example.difyintegration.llm;

import reactor.core.publisher.Mono;

/**
 * LLM客户端接口，用于与大语言模型通信
 * （预留接口，具体实现由用户完成）
 */
public interface LLMClient {
    
    /**
     * 根据用户输入和可用服务列表，决定应执行的服务
     * 
     * @param userInput 用户输入
     * @param availableServices 可用服务列表及其Schema
     * @return 服务决策结果
     */
    Mono<ServiceDecisionResult> decideService(String userInput, String availableServices);
    
    /**
     * 服务决策结果类
     */
    class ServiceDecisionResult {
        private String serviceName;
        private java.util.Map<String, Object> parameters;
        
        public ServiceDecisionResult() {}
        
        public ServiceDecisionResult(String serviceName, java.util.Map<String, Object> parameters) {
            this.serviceName = serviceName;
            this.parameters = parameters;
        }
        
        // Getters and setters
        public String getServiceName() {
            return serviceName;
        }
        
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
        
        public java.util.Map<String, Object> getParameters() {
            return parameters;
        }
        
        public void setParameters(java.util.Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
}