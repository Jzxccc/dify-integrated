package com.example.difyintegration.controller;

import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.service.AppInteractionService;
import com.example.difyintegration.service.DifyAppClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/{appId}")
@RequiredArgsConstructor
public class AppInteractionController {

    private final AppInteractionService appInteractionService;
    private final DifyAppClient difyAppClient;

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> sendAppMessage(@PathVariable String appId, @RequestBody AppChatRequest request) {
        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid app ID"));
        }

        // Validate that query is provided
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Query is required"));
        }

        return appInteractionService.processAppInteraction(appId, request)
                .map(interaction -> ResponseEntity.ok(interaction.getOutput()))
                .onErrorReturn(ResponseEntity.status(500).body("Error processing app interaction"));
    }

    @PostMapping("/chat-simple")
    public Mono<ResponseEntity<String>> sendSimpleAppMessage(@PathVariable String appId, @RequestBody Map<String, String> requestBody) {
        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid app ID"));
        }

        // Extract query from request body
        String query = requestBody.get("query");
        String user = requestBody.get("user"); // Optional user identifier

        // Validate that query is provided
        if (query == null || query.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Query is required"));
        }

        // Create AppChatRequest with just the query and optional user
        String actualUser = user != null && !user.trim().isEmpty() ? user : "default_user";
        AppChatRequest request = AppChatRequest.builder()
                .query(query)
                .user(actualUser)
                .responseMode("blocking") // Default to blocking
                .build();

        return appInteractionService.processAppInteraction(appId, request)
                .map(interaction -> ResponseEntity.ok(interaction.getOutput()))
                .onErrorReturn(ResponseEntity.status(500).body("Error processing app interaction"));
    }

    @PostMapping(value = "/chat-stream", produces = "text/plain")
    public Flux<String> chatStream(@PathVariable String appId, @RequestBody AppChatRequest request) {
        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            // For validation errors, we might want to throw an exception or handle differently
            // For now, return an empty flux
            return Flux.empty();
        }

        // Validate that query is provided
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return Flux.empty();
        }

        // Create DifyChatRequest with default user if not provided
        DifyChatRequest difyRequest;
        if (request.getUser() == null || request.getUser().trim().isEmpty()) {
            difyRequest = DifyChatRequest.builder()
                    .inputs(request.getInputs())
                    .query(request.getQuery())
                    .responseMode("streaming")  // Force streaming mode
                    .conversationId(request.getConversationId())
                    .user("default_user")
                    .files(request.getFiles())
                    .build();
        } else {
            difyRequest = DifyChatRequest.builder()
                    .inputs(request.getInputs())
                    .query(request.getQuery())
                    .responseMode("streaming")  // Force streaming mode
                    .conversationId(request.getConversationId())
                    .user(request.getUser())
                    .files(request.getFiles())
                    .build();
        }

        return difyAppClient.sendAppStreamMessage(appId, difyRequest);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AppInteraction>> getAppInteractionHistory(
            @PathVariable String appId,
            @RequestParam(required = false) String userId) {

        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            return ResponseEntity.badRequest().build();
        }

        List<AppInteraction> interactions;
        if (userId != null && !userId.isEmpty()) {
            interactions = appInteractionService.getInteractionsByUserId(userId);
        } else {
            interactions = appInteractionService.getInteractionsByAppId(appId);
        }

        return ResponseEntity.ok(interactions);
    }
}