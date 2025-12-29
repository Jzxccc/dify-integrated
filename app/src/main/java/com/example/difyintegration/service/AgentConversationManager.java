package com.example.difyintegration.service;

import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentConversationManager {

    private final DifyApiClient difyApiClient;

    /**
     * Sends a message to the Dify agent and handles any required follow-up messages
     * automatically to ensure a complete conversation flow.
     */
    public Mono<DifyChatResponse> processConversation(DifyChatRequest request) {
        // For now, we'll just send the message directly
        // In a more complex implementation, we would analyze the response
        // to determine if additional messages are needed
        return difyApiClient.sendMessage(request)
                .flatMap(response -> handleResponseIfNeeded(request, response));
    }

    /**
     * Handles the response from Dify and determines if additional messages are needed
     */
    private Mono<DifyChatResponse> handleResponseIfNeeded(DifyChatRequest originalRequest, DifyChatResponse response) {
        // In a real implementation, we would analyze the response to see if
        // additional messages are required to complete the conversation
        // For example, if the response indicates that more information is needed

        log.debug("Processing response: {}", response);

        // For now, just return the response as-is
        // In a more sophisticated implementation, we would check the response
        // and potentially send follow-up messages
        return Mono.just(response);
    }

    /**
     * Creates a follow-up request based on the previous response
     */
    private DifyChatRequest createFollowUpRequest(DifyChatRequest originalRequest, DifyChatResponse response) {
        return DifyChatRequest.builder()
                .inputs(originalRequest.getInputs())
                .query("Please continue with the next step based on the previous response.") // This would be more sophisticated in real implementation
                .conversationId(response.getConversationId())
                .user(originalRequest.getUser())
                .responseMode(originalRequest.getResponseMode())
                .build();
    }
}