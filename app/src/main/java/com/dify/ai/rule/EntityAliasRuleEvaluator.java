package com.dify.ai.rule;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ENTITY_ALIAS atomic rule evaluator.
 * Checks if the user input contains any entity alias (e.g., "SF" for "顺丰").
 */
@Component
public class EntityAliasRuleEvaluator implements AtomicRuleEvaluator {
    
    @Override
    public boolean evaluate(String userInput, Map<String, Object> params) {
        if (userInput == null || userInput.isEmpty()) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, String> aliases = (Map<String, String>) params.get("aliases");
        if (aliases == null || aliases.isEmpty()) {
            return false;
        }
        
        String lowerUserInput = userInput.toLowerCase();
        
        // Check if any alias appears in the user input
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String alias = entry.getKey().toLowerCase();
            String entity = entry.getValue().toLowerCase();
            
            if (lowerUserInput.contains(alias) || lowerUserInput.contains(entity)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getType() {
        return "ENTITY_ALIAS";
    }
}