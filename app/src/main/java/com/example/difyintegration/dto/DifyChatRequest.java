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
public class DifyChatRequest {
    
    @JsonProperty("inputs")
    private Map<String, Object> inputs;
    
    @JsonProperty("query")
    private String query;
    
    @JsonProperty("response_mode")
    private String responseMode = "streaming"; // or "blocking"
    
    @JsonProperty("conversation_id")
    private String conversationId; // Optional, for continuing a conversation
    
    @JsonProperty("user")
    private String user; // Optional, user identifier
    
    @JsonProperty("files")
    private Object[] files; // Optional, for file uploads
}