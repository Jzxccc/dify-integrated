package com.example.difyintegration.controller;

import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import com.example.difyintegration.service.AgentConversationManager;
import com.example.difyintegration.service.ApiKeyService;
import com.example.difyintegration.service.DifyApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DifyController {

    private final AgentConversationManager agentConversationManager;
    private final ApiKeyService apiKeyService;
    private final DifyApiClient difyApiClient;

    @PostMapping("/chat")
    public Mono<ResponseEntity<DifyChatResponse>> sendMessage(@RequestBody DifyChatRequest request) {
        // Create a new request with default user if not provided
        DifyChatRequest updatedRequest;
        if (request.getUser() == null || request.getUser().trim().isEmpty()) {
            updatedRequest = DifyChatRequest.builder()
                    .inputs(request.getInputs())
                    .query(request.getQuery())
                    .responseMode(request.getResponseMode())
                    .conversationId(request.getConversationId())
                    .user("default_user")  // Set default user
                    .files(request.getFiles())
                    .build();
        } else {
            updatedRequest = request;
        }

        // For blocking mode, process normally
        return agentConversationManager.processConversation(updatedRequest)
                .map(response -> ResponseEntity.ok(response))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody DifyChatRequest request) {
        // Create a new request with default user if not provided
        DifyChatRequest updatedRequest;
        if (request.getUser() == null || request.getUser().trim().isEmpty()) {
            updatedRequest = DifyChatRequest.builder()
                    .inputs(request.getInputs())
                    .query(request.getQuery())
                    .responseMode("streaming")  // Force streaming mode
                    .conversationId(request.getConversationId())
                    .user("default_user")  // Set default user
                    .files(request.getFiles())
                    .build();
        } else {
            updatedRequest = DifyChatRequest.builder()
                    .inputs(request.getInputs())
                    .query(request.getQuery())
                    .responseMode("streaming")  // Force streaming mode
                    .conversationId(request.getConversationId())
                    .user(request.getUser())
                    .files(request.getFiles())
                    .build();
        }

        // For streaming response, return a stream directly
        return difyApiClient.sendStreamMessage(updatedRequest);
    }

    @PostMapping("/config/api-key")
    public Mono<ResponseEntity<String>> setApiKey(@RequestBody Map<String, String> requestBody) {
        String apiKey = requestBody.get("apiKey");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("API key is required"));
        }

        return Mono.fromRunnable(() -> apiKeyService.saveApiKey(apiKey))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.defer(() -> Mono.just(ResponseEntity.ok("API key saved successfully"))))
                .onErrorReturn(ResponseEntity.status(500).body("Error saving API key: " + "Exception occurred"));
    }

    @GetMapping("/config/api-key")
    public Mono<ResponseEntity<Boolean>> checkApiKey() {
        return apiKeyService.getApiKey()
                .map(apiKey -> ResponseEntity.ok(true))
                .switchIfEmpty(Mono.just(ResponseEntity.ok(false)));
    }
}