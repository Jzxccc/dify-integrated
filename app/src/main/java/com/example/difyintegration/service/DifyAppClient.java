package com.example.difyintegration.service;

import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DifyAppClient {

    private final WebClient difyWebClient;
    private final ApiKeyService apiKeyService;

    @Value("${dify.api.base-url}")
    private String difyApiBaseUrl;

    public Mono<DifyChatResponse> sendAppMessage(String appId, DifyChatRequest request) {
        return apiKeyService.getApiKey()
                .flatMap(apiKey -> {
                    log.debug("Sending app request to Dify API: {} for app: {}", request, appId);
                    log.debug("API Key is configured: {}", apiKey != null);
                    log.debug("App ID: {}", appId);
                    log.debug("Request body: {}", request);

                    // Determine if we expect streaming or blocking response
                    boolean isStreaming = "streaming".equalsIgnoreCase(request.getResponseMode());

                    var webClientRequest = difyWebClient.post()
                            .uri("/apps/" + appId + "/chat-messages")  // 应用特定的聊天消息端点
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .bodyValue(request);

                    if (isStreaming) {
                        // For streaming, we expect text/event-stream response
                        return webClientRequest
                                .retrieve()
                                .bodyToFlux(String.class)  // For streaming response
                                .collectList()
                                .map(responseParts -> {
                                    String fullResponse = String.join("", responseParts);
                                    log.debug("Received streaming response: {}", fullResponse);
                                    // Create a DifyChatResponse from the streaming response
                                    DifyChatResponse chatResponse = new DifyChatResponse();
                                    chatResponse.setText(fullResponse);
                                    return chatResponse;
                                });
                    } else {
                        // For blocking, we expect JSON response
                        return webClientRequest
                                .retrieve()
                                .bodyToMono(DifyChatResponse.class)
                                .doOnSuccess(response -> log.debug("Received response from Dify API for app {}: {}", appId, response));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("API key not configured")))
                .doOnError(error -> {
                    log.error("Error calling Dify API for app {}: ", appId, error);
                    if (error instanceof WebClientResponseException) {
                        log.error("Response body: {}", ((WebClientResponseException) error).getResponseBodyAsString());
                        log.error("Response status: {}", ((WebClientResponseException) error).getStatusCode());
                    }
                });
    }

    public Flux<String> sendAppStreamMessage(String appId, DifyChatRequest request) {
        return apiKeyService.getApiKey()
                .flatMapMany(apiKey -> {
                    log.debug("Sending app stream request to Dify API: {} for app: {}", request, appId);

                    return difyWebClient.post()
                            .uri("/apps/" + appId + "/chat-messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .bodyValue(request)
                            .retrieve()
                            .bodyToFlux(String.class)  // Return the stream directly
                            .doOnNext(data -> log.debug("Received stream data for app {}: {}", appId, data))
                            .doOnError(error -> log.error("Error in app stream for app {}: ", appId, error));
                })
                .switchIfEmpty(Flux.error(new RuntimeException("API key not configured")));
    }
}