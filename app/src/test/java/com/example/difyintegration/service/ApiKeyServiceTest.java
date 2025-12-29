package com.example.difyintegration.service;

import com.example.difyintegration.entity.ApiKey;
import com.example.difyintegration.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    private ApiKeyService apiKeyService;

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(apiKeyRepository);
    }

    @Test
    void shouldSaveApiKey() {
        // Given
        String apiKeyValue = "test-api-key";

        // When
        apiKeyService.saveApiKey(apiKeyValue);

        // Then
        verify(apiKeyRepository, times(1)).save(any(ApiKey.class));
    }

    @Test
    void shouldGetApiKey() {
        // Given
        ApiKey apiKey = new ApiKey();
        apiKey.setApiKeyValue("test-api-key");
        when(apiKeyRepository.findFirstByOrderByUpdatedAtDesc()).thenReturn(apiKey);

        // When
        var result = apiKeyService.getApiKey();

        // Then
        assertTrue(result.isPresent());
        assertEquals("test-api-key", result.get());
    }

    @Test
    void shouldReturnEmptyWhenNoApiKeyExists() {
        // Given
        when(apiKeyRepository.findFirstByOrderByUpdatedAtDesc()).thenReturn(null);

        // When
        var result = apiKeyService.getApiKey();

        // Then
        assertTrue(result.isEmpty());
    }
}