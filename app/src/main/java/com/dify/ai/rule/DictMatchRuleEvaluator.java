package com.dify.ai.rule;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * DICT_MATCH atomic rule evaluator.
 * Checks if the user input contains any term from a predefined dictionary.
 */
@Component
public class DictMatchRuleEvaluator implements AtomicRuleEvaluator {
    
    @Override
    public boolean evaluate(String userInput, Map<String, Object> params) {
        if (userInput == null || userInput.isEmpty()) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        List<String> dictionary = (List<String>) params.get("dictionary");
        if (dictionary == null || dictionary.isEmpty()) {
            return false;
        }
        
        String lowerUserInput = userInput.toLowerCase();
        
        // Check if any dictionary term appears in the user input
        for (String term : dictionary) {
            if (lowerUserInput.contains(term.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String getType() {
        return "DICT_MATCH";
    }
}