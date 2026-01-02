package com.example.difyintegration.controller;

import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.dto.AppChatRequestDto;
import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.entity.Conversation;
import com.example.difyintegration.entity.User;
import com.example.difyintegration.service.AppInteractionService;
import com.example.difyintegration.service.ConversationService;
import com.example.difyintegration.service.DifyAppClient;
import com.example.difyintegration.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authenticated/app/{appId}")
@RequiredArgsConstructor
public class AuthenticatedAppController {

    private final AppInteractionService appInteractionService;
    private final ConversationService conversationService;
    private final DifyAppClient difyAppClient;
    private final UserService userService;

    @PostMapping("/chat")
    public Mono<ResponseEntity<String>> sendAppMessage(
            @PathVariable String appId,
            @RequestBody AppChatRequestDto request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid app ID"));
        }

        // Validate that query is provided
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("Query is required"));
        }

        // If a conversationId is provided, verify the user has access to it
        if (request.getConversationId() != null) {
            Optional<Conversation> conversationOpt = conversationService.findByIdAndUser(request.getConversationId(), user);
            if (conversationOpt.isEmpty()) {
                return Mono.just(ResponseEntity.status(403).body("Access denied to conversation"));
            }
        }

        // Create a new AppChatRequest with the user ID
        AppChatRequest appChatRequest = AppChatRequest.builder()
                .query(request.getQuery())
                .user(user.getUserId()) // Use the authenticated user's ID
                .inputs(request.getInputs())
                .responseMode(request.getResponseMode())
                .conversationId(request.getConversationId())
                .files(request.getFiles())
                .build();

        return appInteractionService.processAppInteraction(appId, appChatRequest)
                .map(interaction -> ResponseEntity.ok(interaction.getOutput()))
                .onErrorReturn(ResponseEntity.status(500).body("Error processing app interaction"));
    }

    @PostMapping(value = "/chat-stream", produces = "text/plain")
    public Flux<String> chatStream(
            @PathVariable String appId,
            @RequestBody AppChatRequestDto request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        // Validate the app ID
        if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
            return Flux.empty();
        }

        // Validate that query is provided
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return Flux.empty();
        }

        // If a conversationId is provided, verify the user has access to it
        if (request.getConversationId() != null) {
            Optional<Conversation> conversationOpt = conversationService.findByIdAndUser(request.getConversationId(), user);
            if (conversationOpt.isEmpty()) {
                return Flux.error(new RuntimeException("Access denied to conversation"));
            }
        }

        // Create DifyChatRequest with authenticated user
        DifyChatRequest difyRequest = DifyChatRequest.builder()
                .inputs(request.getInputs())
                .query(request.getQuery())
                .responseMode("streaming")  // Force streaming mode
                .conversationId(request.getConversationId())
                .user(user.getUserId()) // Use the authenticated user's ID
                .files(request.getFiles())
                .build();

        return difyAppClient.sendAppStreamMessage(appId, difyRequest);
    }

    @GetMapping("/history")
    public Mono<ResponseEntity<List<AppInteraction>>> getAppInteractionHistory(
            @PathVariable String appId,
            @RequestParam(required = false) String conversationId,
            Authentication authentication) {

        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);

            // Validate the app ID
            if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
                return ResponseEntity.badRequest().body(java.util.Collections.<AppInteraction>emptyList());
            }

            List<AppInteraction> interactions;

            if (conversationId != null) {
                // If conversationId is provided, check if user has access to it
                Optional<Conversation> conversationOpt = conversationService.findByIdAndUser(conversationId, user);
                if (conversationOpt.isEmpty()) {
                    return ResponseEntity.status(403).body(java.util.Collections.<AppInteraction>emptyList());
                }
                // Get interactions for specific conversation
                interactions = appInteractionService.getInteractionsByConversationId(conversationId);
            } else {
                // Get all interactions for the authenticated user with this app
                interactions = appInteractionService.getInteractionsByAppIdAndUserId(appId, user.getUserId());
            }

            return ResponseEntity.ok(interactions);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/conversations")
    public Mono<ResponseEntity<List<Conversation>>> getUserConversations(
            @PathVariable String appId,
            Authentication authentication) {

        return Mono.fromCallable(() -> {
            User user = getCurrentUser(authentication);

            // Validate the app ID
            if (!"d2a5c47c-5644-49f0-bc20-6a67ac1a7b69".equals(appId)) {
                return ResponseEntity.badRequest().body(java.util.Collections.<Conversation>emptyList());
            }

            List<Conversation> conversations = conversationService.findByUser(user);

            // Filter conversations by app ID
            List<Conversation> appConversations = conversations.stream()
                    .filter(conv -> appId.equals(conv.getAppId()))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(appConversations);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}