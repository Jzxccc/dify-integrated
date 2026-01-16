package com.example.difyintegration.service;

import com.example.difyintegration.annotation.AIService;
import com.example.difyintegration.annotation.AIParam;
import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.repository.AppInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppInteractionService {

    private final DifyAppClient difyAppClient;
    private final AppInteractionRepository appInteractionRepository;

    @AIService(
        name = "process_app_interaction",
        description = "处理应用交互",
        requiresAuth = true
    )
    public Mono<AppInteraction> processAppInteraction(
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId,
        @AIParam(name = "request", description = "应用聊天请求", type = "object", required = true)
        AppChatRequest request) {
        log.info("Processing app interaction for app: {}", appId);

        // Create a new interaction record
        AppInteraction interaction = new AppInteraction();
        interaction.setAppId(appId);
        interaction.setUserId(request.getUser());
        interaction.setInput(request.getQuery());
        interaction.setTimestamp(LocalDateTime.now());

        // If a conversation ID is provided, set it in the interaction
        interaction.setConversationId(request.getConversationId());

        // Save the interaction with input data before sending to Dify
        AppInteraction savedInteraction = appInteractionRepository.save(interaction);

        // Convert AppChatRequest to DifyChatRequest
        // Use inputs from the request or an empty map if not provided
        java.util.Map<String, Object> inputs = request.getInputs() != null ? request.getInputs() : Collections.emptyMap();

        // Ensure user is provided, use a default if not
        String user = request.getUser() != null && !request.getUser().trim().isEmpty()
                     ? request.getUser()
                     : "default_user";

        var difyRequest = com.example.difyintegration.dto.DifyChatRequest.builder()
                .inputs(inputs)
                .query(request.getQuery())
                .responseMode(request.getResponseMode())
                .conversationId(request.getConversationId())
                .user(user)
                .files(request.getFiles())
                .build();

        // Send the request to the Dify API for the specific app
        return difyAppClient.sendAppMessage(appId, difyRequest)
                .map(response -> {
                    // Update the interaction with the response
                    savedInteraction.setOutput(response.getText());
                    savedInteraction.setTimestamp(LocalDateTime.now());

                    // Save the updated interaction with the response
                    return appInteractionRepository.save(savedInteraction);
                })
                .doOnError(error -> {
                    log.error("Error processing app interaction for app: {}", appId, error);
                    // Update the interaction with error information
                    savedInteraction.setOutput("Error: " + error.getMessage());
                    appInteractionRepository.save(savedInteraction);
                });
    }

    @AIService(
        name = "get_interactions_by_app_id",
        description = "根据应用ID获取交互记录",
        requiresAuth = true
    )
    public List<AppInteraction> getInteractionsByAppId(
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId) {
        return appInteractionRepository.findByAppId(appId);
    }

    @AIService(
        name = "get_interactions_by_user_id",
        description = "根据用户ID获取交互记录",
        requiresAuth = true
    )
    public List<AppInteraction> getInteractionsByUserId(
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId) {
        return appInteractionRepository.findByUserId(userId);
    }

    @AIService(
        name = "get_interactions_by_conversation_id",
        description = "根据会话ID获取交互记录",
        requiresAuth = true
    )
    public List<AppInteraction> getInteractionsByConversationId(
        @AIParam(name = "conversationId", description = "会话ID", type = "string", required = true)
        String conversationId) {
        return appInteractionRepository.findByConversationId(conversationId);
    }

    @AIService(
        name = "get_interactions_by_app_id_and_user_id",
        description = "根据应用ID和用户ID获取交互记录",
        requiresAuth = true
    )
    public List<AppInteraction> getInteractionsByAppIdAndUserId(
        @AIParam(name = "appId", description = "应用ID", type = "string", required = true)
        String appId,
        @AIParam(name = "userId", description = "用户ID", type = "string", required = true)
        String userId) {
        return appInteractionRepository.findByAppIdAndUserId(appId, userId);
    }
}