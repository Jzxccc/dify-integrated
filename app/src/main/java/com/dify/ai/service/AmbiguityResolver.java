package com.dify.ai.service;

import com.dify.ai.domain.model.FactValue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AmbiguityResolver handles resolving ambiguities when multiple candidate values exist for the same FactSlot.
 * It implements the ambiguity resolution mechanism as described in the design.
 */
@Service
public class AmbiguityResolver {

    /**
     * Resolves ambiguity when multiple candidate values exist for the same FactSlot.
     * This is a simplified implementation - in a real system, this would be more sophisticated.
     *
     * @param candidateValues the list of candidate FactValues for the same FactSlot
     * @return the most appropriate FactValue, or null if no clear choice exists
     */
    public FactValue resolveAmbiguity(List<FactValue> candidateValues) {
        if (candidateValues == null || candidateValues.isEmpty()) {
            return null;
        }
        
        if (candidateValues.size() == 1) {
            // No ambiguity, return the single value
            return candidateValues.get(0);
        }
        
        // Strategy 1: Prioritize values with stronger evidence
        List<FactValue> highestConfidenceValues = findHighestConfidenceValues(candidateValues);
        
        if (highestConfidenceValues.size() == 1) {
            return highestConfidenceValues.get(0);
        }
        
        // Strategy 2: If still ambiguous, prioritize REVIEWED over RAW
        List<FactValue> reviewedValues = highestConfidenceValues.stream()
                .filter(v -> v.getState() == FactValue.State.REVIEWED)
                .collect(Collectors.toList());
        
        if (reviewedValues.size() == 1) {
            return reviewedValues.get(0);
        } else if (reviewedValues.size() > 1) {
            // If multiple REVIEWED values, return the first one
            return reviewedValues.get(0);
        }
        
        // Strategy 3: If all are RAW, return the first one (or return null to indicate ambiguity)
        // In a real implementation, you might want to return null to indicate that user clarification is needed
        return highestConfidenceValues.get(0);
    }
    
    /**
     * Finds the FactValues with the highest confidence/evidence strength.
     * This is a simplified approach based on evidence length as a proxy for detail.
     *
     * @param candidateValues the list of candidate FactValues
     * @return the list of FactValues with the highest confidence
     */
    private List<FactValue> findHighestConfidenceValues(List<FactValue> candidateValues) {
        // For this simple implementation, we'll use the length of the evidence string as a proxy for confidence
        int maxLength = candidateValues.stream()
                .mapToInt(v -> v.getEvidence() != null ? v.getEvidence().length() : 0)
                .max()
                .orElse(0);
        
        return candidateValues.stream()
                .filter(v -> (v.getEvidence() != null ? v.getEvidence().length() : 0) == maxLength)
                .collect(Collectors.toList());
    }
    
    /**
     * Determines if there is ambiguity among the candidate values.
     *
     * @param candidateValues the list of candidate FactValues for the same FactSlot
     * @return true if there is ambiguity, false otherwise
     */
    public boolean hasAmbiguity(List<FactValue> candidateValues) {
        if (candidateValues == null || candidateValues.size() <= 1) {
            return false;
        }
        
        // Even after applying resolution strategies, if we still have multiple candidates, there's ambiguity
        FactValue resolved = resolveAmbiguity(candidateValues);
        return resolved == null || (resolved != null && 
                candidateValues.stream().anyMatch(v -> !v.equals(resolved)));
    }
}