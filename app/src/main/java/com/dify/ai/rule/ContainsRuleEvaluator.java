package com.dify.ai.rule;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * CONTAINS atomic rule evaluator.
 * Checks if the user input contains a specific keyword or phrase.
 */
@Component
public class ContainsRuleEvaluator implements AtomicRuleEvaluator {
    
    @Override
    public boolean evaluate(String userInput, Map<String, Object> params) {
        if (userInput == null || userInput.isEmpty()) {
            return false;
        }
        
        String keyword = (String) params.get("keyword");
        if (keyword == null || keyword.isEmpty()) {
            return false;
        }
        
        // Perform case-insensitive contains check
        return userInput.toLowerCase().contains(keyword.toLowerCase());
    }
    
    @Override
    public String getType() {
        return "CONTAINS";
    }
}