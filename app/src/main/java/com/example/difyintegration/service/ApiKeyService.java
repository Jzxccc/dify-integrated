package com.example.difyintegration.service;

import com.example.difyintegration.entity.ApiKey;
import com.example.difyintegration.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Mono<String> getApiKey() {
        ApiKey key = apiKeyRepository.findFirstByOrderByUpdatedAtDesc();
        return key != null ? Mono.just(key.getApiKeyValue()) : Mono.empty();
    }

    public void saveApiKey(String apiKeyValue) {
        // In a real application, you might want to encrypt the API key
        ApiKey apiKey = new ApiKey();
        apiKey.setApiKeyValue(apiKeyValue);
        apiKeyRepository.save(apiKey);
    }
}