package com.example.difyintegration.service;

import com.example.difyintegration.dto.AppChatRequest;
import com.example.difyintegration.dto.DifyChatRequest;
import com.example.difyintegration.dto.DifyChatResponse;
import com.example.difyintegration.entity.AppInteraction;
import com.example.difyintegration.repository.AppInteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppInteractionServiceTest {

    @Mock
    private DifyAppClient difyAppClient;

    @Mock
    private AppInteractionRepository appInteractionRepository;

    private AppInteractionService appInteractionService;

    @BeforeEach
    void setUp() {
        appInteractionService = new AppInteractionService(difyAppClient, appInteractionRepository);
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
        var mockResponse = new DifyChatResponse();
        mockResponse.setText("Test response");
        when(difyAppClient.sendAppMessage(eq(appId), any(DifyChatRequest.class))).thenReturn(Mono.just(mockResponse));

        // Mock the repository save method
        AppInteraction savedInteraction = new AppInteraction();
        savedInteraction.setOutput("Test response");
        when(appInteractionRepository.save(any(AppInteraction.class))).thenReturn(savedInteraction);

        // When
        var result = appInteractionService.processAppInteraction(appId, request).block();

        // Then
        assertNotNull(result);
        assertEquals("Test response", result.getOutput());

        // Verify that the repository save was called
        verify(appInteractionRepository, times(1)).save(any(AppInteraction.class));

        // Verify that the app client was called with the correct parameters
        ArgumentCaptor<DifyChatRequest> requestCaptor = ArgumentCaptor.forClass(DifyChatRequest.class);
        verify(difyAppClient).sendAppMessage(eq(appId), requestCaptor.capture());

        DifyChatRequest capturedRequest = requestCaptor.getValue();
        assertEquals("Test query", capturedRequest.getQuery());
        assertEquals("test-user", capturedRequest.getUser());
        assertEquals("blocking", capturedRequest.getResponseMode());
    }

    @Test
    void shouldGetInteractionsByAppId() {
        // Given
        String appId = "d2a5c47c-5644-49f0-bc20-6a67ac1a7b69";
        AppInteraction interaction1 = new AppInteraction();
        interaction1.setAppId(appId);
        AppInteraction interaction2 = new AppInteraction();
        interaction2.setAppId(appId);
        List<AppInteraction> expectedInteractions = List.of(interaction1, interaction2);

        when(appInteractionRepository.findByAppIdOrderByTimestampDesc(appId)).thenReturn(expectedInteractions);

        // When
        var result = appInteractionService.getInteractionsByAppId(appId);

        // Then
        assertEquals(2, result.size());
        verify(appInteractionRepository).findByAppIdOrderByTimestampDesc(appId);
    }

    @Test
    void shouldGetInteractionsByUserId() {
        // Given
        String userId = "test-user";
        AppInteraction interaction1 = new AppInteraction();
        interaction1.setUserId(userId);
        AppInteraction interaction2 = new AppInteraction();
        interaction2.setUserId(userId);
        List<AppInteraction> expectedInteractions = List.of(interaction1, interaction2);

        when(appInteractionRepository.findByUserIdOrderByTimestampDesc(userId)).thenReturn(expectedInteractions);

        // When
        var result = appInteractionService.getInteractionsByUserId(userId);

        // Then
        assertEquals(2, result.size());
        verify(appInteractionRepository).findByUserIdOrderByTimestampDesc(userId);
    }
}