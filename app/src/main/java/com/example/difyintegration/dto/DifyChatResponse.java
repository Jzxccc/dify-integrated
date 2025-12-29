package com.example.difyintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DifyChatResponse {
    
    private String event;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    @JsonProperty("id")
    private String messageId;
    
    @JsonProperty("answer")
    private String text;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("metadata")
    private Metadata metadata;
    
    @Data
    public static class Metadata {
        @JsonProperty("usage")
        private Usage usage;
        
        @JsonProperty("retrieval_resources")
        private List<RetrievalResource> retrievalResources;
    }
    
    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("prompt_unit_price")
        private String promptUnitPrice;
        
        @JsonProperty("prompt_price_unit")
        private String promptPriceUnit;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("completion_unit_price")
        private String completionUnitPrice;
        
        @JsonProperty("completion_price_unit")
        private String completionPriceUnit;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
        
        @JsonProperty("total_price")
        private String totalPrice;
        
        @JsonProperty("currency")
        private String currency;
        
        @JsonProperty("latency")
        private Double latency;
    }
    
    @Data
    public static class RetrievalResource {
        private String position;
        private String datasetName;
        private String documentName;
        private String segmentId;
        private String content;
    }
}