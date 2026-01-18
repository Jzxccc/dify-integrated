package com.example.difyintegration.llm;

import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * LLM决策服务，接收用户输入，调用LLM决定需要执行的服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LLMDecisionService {

    private final LLMClient llmClient;
    private final IntentParser intentParser;
    private final ServiceMapper serviceMapper;
    private final IntelligentServiceInvoker intelligentServiceInvoker;
    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 根据用户输入决定并执行服务
     */
    public Mono<Object> processUserInput(String userInput) {
        log.info("Processing user input with LLM decision: {}", userInput);
        
        // 解析用户意图
        IntentParser.ParsedIntent parsedIntent = intentParser.parseIntent(userInput);
        
        // 获取可用服务列表
        String availableServices = getAvailableServicesAsJson();
        
        // 调用LLM决定应执行的服务
        return llmClient.decideService(userInput, availableServices)
                .flatMap(decisionResult -> {
                    if (decisionResult.getServiceName() != null) {
                        // 使用LLM决策结果执行服务
                        return intelligentServiceInvoker.executeService(
                                decisionResult.getServiceName(),
                                decisionResult.getParameters()
                        );
                    } else {
                        // 如果LLM没有返回明确的服务决策，尝试使用服务映射器
                        return serviceMapper.findMatchingService(userInput, parsedIntent.getParameters())
                                .map(serviceInfo -> {
                                    String serviceName = serviceInfo.getAIServiceAnnotation().name().isEmpty()
                                        ? serviceInfo.getMethod().getName()
                                        : serviceInfo.getAIServiceAnnotation().name();

                                    // 合并解析的参数和LLM决策的参数
                                    Map<String, Object> combinedParams = parsedIntent.getParameters();
                                    if (decisionResult.getParameters() != null) {
                                        combinedParams.putAll(decisionResult.getParameters());
                                    }

                                    return intelligentServiceInvoker.executeService(serviceName, combinedParams);
                                })
                                .orElse(Mono.error(new RuntimeException("No matching service found for input: " + userInput)));
                    }
                });
    }

    /**
     * 获取可用服务列表的JSON表示
     */
    private String getAvailableServicesAsJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        
        var allServices = aiServiceRegistry.getAllServices();
        boolean first = true;
        for (var serviceInfo : allServices) {
            if (!first) {
                sb.append(",\n");
            }
            first = false;
            
            String serviceName = serviceInfo.getAIServiceAnnotation().name().isEmpty() 
                ? serviceInfo.getMethod().getName() 
                : serviceInfo.getAIServiceAnnotation().name();
            
            sb.append("  \"").append(serviceName).append("\": {\n");
            sb.append("    \"description\": \"").append(serviceInfo.getAIServiceAnnotation().description()).append("\",\n");
            sb.append("    \"category\": \"").append(serviceInfo.getCategory()).append("\",\n");
            sb.append("    \"schema\": ").append(serviceInfo.getSchema()).append("\n");
            sb.append("  }");
        }
        
        sb.append("\n}");
        return sb.toString();
    }
}