package com.dify.ai.rule;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * REGEX atomic rule evaluator.
 * Checks if the user input matches a specific regular expression pattern.
 */
@Component
public class RegexRuleEvaluator implements AtomicRuleEvaluator {
    
    @Override
    public boolean evaluate(String userInput, Map<String, Object> params) {
        if (userInput == null || userInput.isEmpty()) {
            return false;
        }
        
        String patternStr = (String) params.get("pattern");
        if (patternStr == null || patternStr.isEmpty()) {
            return false;
        }
        
        try {
            Pattern pattern = Pattern.compile(patternStr);
            return pattern.matcher(userInput).find();
        } catch (Exception e) {
            // Log the exception in a real implementation
            return false;
        }
    }
    
    @Override
    public String getType() {
        return "REGEX";
    }
}