package com.dify.ai.service;

import org.springframework.stereotype.Service;

/**
 * ClarificationRequestGenerator creates clarifying questions for the user when input is ambiguous.
 * It implements the user clarification request functionality as described in the design.
 */
@Service
public class ClarificationRequestGenerator {

    /**
     * Generates a clarifying question for the user when input is ambiguous or insufficient.
     *
     * @param factSlotId the ID of the fact that needs clarification
     * @param currentValue the current value that is ambiguous
     * @param context additional context about the situation
     * @return a clarifying question for the user
     */
    public String generateClarificationRequest(String factSlotId, Object currentValue, String context) {
        if (factSlotId == null || factSlotId.trim().isEmpty()) {
            return "Could you please clarify your request?";
        }
        
        // Generate different types of questions based on the fact type
        switch (factSlotId.toUpperCase()) {
            case "SUPPLIER_NAME":
                return "Which supplier are you referring to? Could you please specify the supplier name?";
            case "ORDER_ID":
                return "Could you please provide the specific order ID you're looking for?";
            case "DATE_RANGE":
                return "Could you clarify the time period you're interested in?";
            default:
                return "I need more information to understand your request. Could you please clarify?";
        }
    }
    
    /**
     * Generates a generic clarifying question when the specific fact type is unknown.
     *
     * @param factSlotId the ID of the fact that needs clarification
     * @return a generic clarifying question
     */
    public String generateGenericClarificationRequest(String factSlotId) {
        return "Could you please provide more details about " + factSlotId + "?";
    }
    
    /**
     * Generates a clarifying question for ambiguous values.
     *
     * @param factSlotId the ID of the fact that has ambiguous values
     * @param possibleValues the list of possible values that are causing ambiguity
     * @return a clarifying question that presents options to the user
     */
    public String generateDisambiguationRequest(String factSlotId, Object[] possibleValues) {
        if (possibleValues == null || possibleValues.length == 0) {
            return generateGenericClarificationRequest(factSlotId);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("I found multiple possibilities for ").append(factSlotId).append(". Could you please specify which one you mean?\n");
        
        for (int i = 0; i < possibleValues.length; i++) {
            sb.append((i + 1)).append(". ").append(possibleValues[i]).append("\n");
        }
        
        return sb.toString();
    }
}