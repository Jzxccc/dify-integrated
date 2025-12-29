package com.example.difyintegration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${dify.api.base-url}")
    private String difyApiBaseUrl;

    @Bean
    public WebClient difyWebClient() {
        return WebClient.builder()
                .baseUrl(difyApiBaseUrl)
                .build();
    }
}