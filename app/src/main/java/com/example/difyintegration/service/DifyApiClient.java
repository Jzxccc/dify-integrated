package com.example.difyintegration.service;

import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DifyApiClient {

    private final WebClient difyWebClient;
    private final ApiKeyService apiKeyService;

    @Value("${dify.api.base-url}")
    private String difyApiBaseUrl;

    public Mono<DifyChatResponse> sendMessage(DifyChatRequest request) {
        return apiKeyService.getApiKey()
                .flatMap(apiKey -> {
                    log.debug("Sending request to Dify API: {}", request);

                    // Determine if we expect streaming or blocking response
                    boolean isStreaming = "streaming".equalsIgnoreCase(request.getResponseMode());

                    var webClientRequest = difyWebClient.post()
                            .uri("/chat-messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .bodyValue(request);

                    if (isStreaming) {
                        // For streaming, we expect text/event-stream response
                        // This requires handling SSE events properly
                        return webClientRequest
                                .retrieve()
                                .bodyToFlux(String.class)
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
                                .doOnSuccess(response -> log.debug("Received response from Dify API: {}", response));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("API key not configured")))
                .doOnError(error -> {
                    log.error("Error calling Dify API: ", error);
                    if (error instanceof WebClientResponseException) {
                        log.error("Response body: {}", ((WebClientResponseException) error).getResponseBodyAsString());
                        log.error("Response status: {}", ((WebClientResponseException) error).getStatusCode());
                    }
                });
    }

    public Flux<String> sendStreamMessage(DifyChatRequest request) {
        return apiKeyService.getApiKey()
                .flatMapMany(apiKey -> {
                    log.debug("Sending stream request to Dify API: {}", request);

                    return difyWebClient.post()
                            .uri("/chat-messages")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .bodyValue(request)
                            .retrieve()
                            .bodyToFlux(String.class)  // Return the stream directly
                            .doOnNext(data -> log.debug("Received stream data: {}", data))
                            .doOnError(error -> log.error("Error in stream: ", error));
                })
                .switchIfEmpty(Flux.error(new RuntimeException("API key not configured")));
    }
}