package com.example.difyintegration.llm;

import com.example.difyintegration.service.AIServiceExecutor;
import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 智能服务调用器，执行LLM决策后的服务调用
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IntelligentServiceInvoker {

    private final AIServiceExecutor aiServiceExecutor;
    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 执行LLM决策的服务调用
     */
    public Mono<Object> executeService(String serviceName, java.util.Map<String, Object> parameters) {
        log.info("Executing service call based on LLM decision: {} with parameters: {}", serviceName, parameters);
        
        // 验证服务名称是否合法
        if (!aiServiceRegistry.getAllServiceNames().contains(serviceName)) {
            log.error("Invalid service name: {}", serviceName);
            return Mono.error(new IllegalArgumentException("Invalid service name: " + serviceName));
        }
        
        // 执行服务调用
        try {
            Object result = aiServiceExecutor.executeService(serviceName, parameters);
            log.info("Service {} executed successfully", serviceName);
            return Mono.just(result);
        } catch (Exception e) {
            log.error("Error executing service: " + serviceName, e);
            return Mono.error(e);
        }
    }

    /**
     * 执行服务调用并处理结果
     */
    public Mono<ServiceExecutionResult> executeServiceWithResult(String serviceName, java.util.Map<String, Object> parameters) {
        return executeService(serviceName, parameters)
                .map(result -> new ServiceExecutionResult(true, result, null));
    }

    /**
     * 服务执行结果类
     */
    public static class ServiceExecutionResult {
        private final boolean success;
        private final Object result;
        private final String errorMessage;

        public ServiceExecutionResult(boolean success, Object result, String errorMessage) {
            this.success = success;
            this.result = result;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public Object getResult() {
            return result;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}