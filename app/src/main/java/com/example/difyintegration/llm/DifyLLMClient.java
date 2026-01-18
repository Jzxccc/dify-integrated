package com.example.difyintegration.llm;

import com.example.difyintegration.service.AIServiceRegistry;
import com.example.difyintegration.util.AIServiceSchemaGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 基于Dify平台的LLM客户端实现
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DifyLLMClient implements LLMClient {

    private final AIServiceRegistry aiServiceRegistry;
    private final AIServiceSchemaGenerator schemaGenerator;
    private final WebClient difyWebClient; // 使用现有的WebClient

    @Override
    public Mono<ServiceDecisionResult> decideService(String userInput, String availableServices) {
        log.info("Processing LLM decision for input: {}", userInput);

        // TODO: 这里是您需要填充的大模型调用实现
        // 目前返回一个空实现，仅作框架演示
        // 1. 调用Dify或其他LLM平台API
        // 2. 传入用户输入和可用服务信息
        // 3. 解析LLM返回的服务决策和参数
        // 4. 返回ServiceDecisionResult

        // 示例：构建一个提示，让大模型决定应调用哪个服务
        String prompt = buildDecisionPrompt(userInput, availableServices);

        // 这里您需要实现实际的LLM调用逻辑
        // 例如，调用Dify的Completion API或Chat API
        return callLLMWithPrompt(prompt)
                .map(this::parseLLMResponse)
                .switchIfEmpty(Mono.fromCallable(() -> {
                    log.warn("LLM did not return a clear decision for input: {}", userInput);
                    return new ServiceDecisionResult(null, java.util.Map.of());
                }));
    }

    /**
     * 构建决策提示
     */
    private String buildDecisionPrompt(String userInput, String availableServices) {
        return String.format(
            "Given the user input '%s', determine which of the following services should be called:\n%s\n\n" +
            "Respond in JSON format with service name and parameters: {\"serviceName\": \"...\", \"parameters\": {...}}",
            userInput,
            availableServices
        );
    }

    /**
     * 调用LLM API
     * TODO: 您需要实现这部分逻辑
     */
    private Mono<String> callLLMWithPrompt(String prompt) {
        // 这里需要您实现实际的LLM调用逻辑
        // 例如，调用Dify的Completion API或Chat API
        log.info("Calling LLM with prompt: {}", prompt);

        // 临时返回一个模拟响应，您需要替换为实际的LLM调用
        return Mono.just("{\"serviceName\": \"send_message_to_app\", \"parameters\": {\"query\": \"" + prompt + "\"}}");
    }

    /**
     * 解析LLM响应
     */
    private ServiceDecisionResult parseLLMResponse(String llmResponse) {
        try {
            // 使用Jackson解析LLM返回的JSON响应
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> responseMap = mapper.readValue(llmResponse, java.util.Map.class);

            String serviceName = (String) responseMap.get("serviceName");
            java.util.Map<String, Object> parameters = (java.util.Map<String, Object>) responseMap.get("parameters");

            if (parameters == null) {
                parameters = java.util.Map.of();
            }

            return new ServiceDecisionResult(serviceName, parameters);
        } catch (Exception e) {
            log.error("Error parsing LLM response: {}", llmResponse, e);
            return new ServiceDecisionResult(null, java.util.Map.of());
        }
    }
}