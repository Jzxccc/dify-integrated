package com.example.difyintegration.service;

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

    public Mono<AppInteraction> processAppInteraction(String appId, AppChatRequest request) {
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

    public List<AppInteraction> getInteractionsByAppId(String appId) {
        return appInteractionRepository.findByAppId(appId);
    }

    public List<AppInteraction> getInteractionsByUserId(String userId) {
        return appInteractionRepository.findByUserId(userId);
    }

    public List<AppInteraction> getInteractionsByConversationId(String conversationId) {
        return appInteractionRepository.findByConversationId(conversationId);
    }

    public List<AppInteraction> getInteractionsByAppIdAndUserId(String appId, String userId) {
        return appInteractionRepository.findByAppIdAndUserId(appId, userId);
    }
}