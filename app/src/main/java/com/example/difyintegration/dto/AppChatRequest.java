package com.example.difyintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppChatRequest {

    @JsonProperty("inputs")
    private Map<String, Object> inputs; // Optional, may be provided by frontend or defaults from app configuration

    @JsonProperty("query")
    private String query; // Required, user's query

    @JsonProperty("response_mode")
    private String responseMode = "blocking"; // Default to blocking for app integration

    @JsonProperty("conversation_id")
    private String conversationId; // Optional, for continuing a conversation

    @JsonProperty("user")
    private String user; // Optional, user identifier

    @JsonProperty("files")
    private Object[] files; // Optional, for file uploads
}