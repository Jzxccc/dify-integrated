package com.example.difyintegration.service;

import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.repository.AppInteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppInteractionServiceTest {

    @Mock
    private DifyApiClient difyApiClient;

    @Mock
    private AppInteractionRepository appInteractionRepository;

    private AppInteractionService appInteractionService;

    @BeforeEach
    void setUp() {
        appInteractionService = new AppInteractionService(difyApiClient, appInteractionRepository);
    }

    @Test
    void shouldProcessAppInteractionSuccessfully() {
        // Given
        String appId = "d2a5c47c-5644-49f0-bc20-6a67ac1a7b69";
        AppChatRequest request = AppChatRequest.builder()
                .query("Test query")
                .user("test-user")
                .build();

        // Mock the API client response
        var mockResponse = new com.example.difyintegration.dto.DifyChatResponse();
        mockResponse.setText("Test response");
        when(difyApiClient.sendMessage(any(AppChatRequest.class))).thenReturn(Mono.just(mockResponse));

        // Mock the repository save method
        AppInteraction savedInteraction = new AppInteraction();
        savedInteraction.setOutput("Test response");
        when(appInteractionRepository.save(any(AppInteraction.class))).thenReturn(savedInteraction);

        // When
        var result = appInteractionService.processAppInteraction(appId, request).block();

        // Then
        assertNotNull(result);
        assertEquals("Test response", result.getOutput());
        verify(appInteractionRepository, times(2)).save(any(AppInteraction.class)); // Once for initial save, once for update
        verify(difyApiClient).sendMessage(any(AppChatRequest.class));
    }
}