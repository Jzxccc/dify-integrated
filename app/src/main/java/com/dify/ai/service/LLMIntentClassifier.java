package com.dify.ai.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * LLMIntentClassifier provides LLM-based intent classification capability.
 * It implements the LLM-based classification as described in the design.
 */
@Service
public class LLMIntentClassifier {

    /**
     * Classifies user intent using an LLM-based approach.
     * This is a simulated implementation since we don't have an actual LLM connection.
     * In a real implementation, this would connect to an LLM API.
     *
     * @param userInput the user input to classify
     * @param availableIntents the list of possible intents to choose from
     * @return the classified intent, or null if no clear match
     */
    public String classifyIntentWithLLM(String userInput, List<String> availableIntents) {
        if (userInput == null || userInput.trim().isEmpty() || availableIntents == null || availableIntents.isEmpty()) {
            return null;
        }

        // Simulated LLM classification
        // In a real implementation, this would call an LLM API with a prompt like:
        // "Classify the following user input into one of these intents: [availableIntents]. 
        // Input: [userInput]. Respond with only the intent name."
        
        String lowerInput = userInput.toLowerCase();
        
        // Simple keyword-based simulation for demonstration
        for (String intent : availableIntents) {
            if (matchesIntent(lowerInput, intent)) {
                return intent;
            }
        }
        
        // If no specific intent matches, return null or a default "UNKNOWN" intent
        return null;
    }
    
    /**
     * Simulates LLM-based intent classification with confidence scores.
     * Returns both the intent and a confidence score.
     *
     * @param userInput the user input to classify
     * @param availableIntents the list of possible intents to choose from
     * @return a map containing the intent and confidence score
     */
    public Map<String, Object> classifyIntentWithConfidence(String userInput, List<String> availableIntents) {
        String intent = classifyIntentWithLLM(userInput, availableIntents);
        
        // In a real LLM implementation, the LLM would provide its own confidence measure
        // For this simulation, we'll assign a confidence based on how well it matched
        double confidence = intent != null ? 0.85 : 0.0; // Simulated confidence
        
        return Map.of(
            "intent", intent,
            "confidence", confidence
        );
    }
    
    /**
     * Helper method to simulate intent matching.
     * In a real implementation, this would be handled by the LLM.
     *
     * @param lowerInput the lowercase user input
     * @param intent the intent to check against
     * @return true if the input matches the intent, false otherwise
     */
    private boolean matchesIntent(String lowerInput, String intent) {
        // Simple keyword matching for simulation
        switch (intent.toUpperCase()) {
            case "SUPPLIER_QUERY":
                return lowerInput.contains("supplier") || 
                       lowerInput.contains("vendor") || 
                       lowerInput.contains("provide");
            case "ORDER_STATUS":
                return lowerInput.contains("order") || 
                       lowerInput.contains("status") || 
                       lowerInput.contains("track");
            case "PRODUCT_INFO":
                return lowerInput.contains("product") || 
                       lowerInput.contains("item") || 
                       lowerInput.contains("price");
            case "GENERAL_HELP":
                return lowerInput.contains("help") || 
                       lowerInput.contains("support") || 
                       lowerInput.contains("assist");
            default:
                // For other intents, check if the intent name appears in the input
                return lowerInput.contains(intent.toLowerCase());
        }
    }
}