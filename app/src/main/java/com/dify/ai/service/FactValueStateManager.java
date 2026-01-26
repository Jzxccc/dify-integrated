package com.dify.ai.service;

import com.dify.ai.domain.model.FactValue;
import org.springframework.stereotype.Service;

/**
 * FactValueStateManager handles state transitions for FactValues.
 * It manages the transition from RAW to REVIEWED state as described in the design.
 */
@Service
public class FactValueStateManager {

    /**
     * Transitions a FactValue from RAW state to REVIEWED state.
     *
     * @param rawFactValue the RAW FactValue to transition
     * @param reviewer the entity performing the review (could be user or automated system)
     * @param reviewNotes any notes about the review process
     * @return a new FactValue with REVIEWED state, or null if the input wasn't a RAW value
     */
    public FactValue transitionToReviewed(FactValue rawFactValue, String reviewer, String reviewNotes) {
        if (rawFactValue == null) {
            return null;
        }
        
        if (rawFactValue.getState() != FactValue.State.RAW) {
            // Only RAW facts can be transitioned to REVIEWED
            return rawFactValue;
        }
        
        // Create a new FactValue with REVIEWED state
        FactValue reviewedFactValue = FactValue.builder()
                .value(rawFactValue.getValue())
                .state(FactValue.State.REVIEWED)
                .source(rawFactValue.getSource())
                .evidence(enhanceEvidenceWithReview(rawFactValue.getEvidence(), reviewer, reviewNotes))
                .build();
        
        return reviewedFactValue;
    }
    
    /**
     * Transitions a FactValue from REVIEWED state back to RAW state.
     * This might be needed if a mistake was made during review.
     *
     * @param reviewedFactValue the REVIEWED FactValue to transition back
     * @param reason the reason for reverting the review
     * @return a new FactValue with RAW state, or null if the input wasn't a REVIEWED value
     */
    public FactValue transitionToRaw(FactValue reviewedFactValue, String reason) {
        if (reviewedFactValue == null) {
            return null;
        }
        
        if (reviewedFactValue.getState() != FactValue.State.REVIEWED) {
            // Only REVIEWED facts can be transitioned back to RAW
            return reviewedFactValue;
        }
        
        // Create a new FactValue with RAW state
        FactValue rawFactValue = FactValue.builder()
                .value(reviewedFactValue.getValue())
                .state(FactValue.State.RAW)
                .source(reviewedFactValue.getSource())
                .evidence(enhanceEvidenceWithReversion(reviewedFactValue.getEvidence(), reason))
                .build();
        
        return rawFactValue;
    }
    
    /**
     * Checks if a state transition is valid.
     *
     * @param fromState the current state
     * @param toState the target state
     * @return true if the transition is valid, false otherwise
     */
    public boolean isValidTransition(FactValue.State fromState, FactValue.State toState) {
        if (fromState == null || toState == null) {
            return false;
        }
        
        // Valid transitions: RAW -> REVIEWED, REVIEWED -> RAW
        return (fromState == FactValue.State.RAW && toState == FactValue.State.REVIEWED) ||
               (fromState == FactValue.State.REVIEWED && toState == FactValue.State.RAW);
    }
    
    /**
     * Enhances the evidence string with review information.
     *
     * @param originalEvidence the original evidence
     * @param reviewer the entity that performed the review
     * @param reviewNotes notes about the review
     * @return enhanced evidence string
     */
    private String enhanceEvidenceWithReview(String originalEvidence, String reviewer, String reviewNotes) {
        StringBuilder enhancedEvidence = new StringBuilder();
        
        if (originalEvidence != null) {
            enhancedEvidence.append(originalEvidence).append("; ");
        }
        
        enhancedEvidence.append("Reviewed by: ").append(reviewer != null ? reviewer : "unknown");
        
        if (reviewNotes != null && !reviewNotes.trim().isEmpty()) {
            enhancedEvidence.append(", Notes: ").append(reviewNotes);
        }
        
        return enhancedEvidence.toString();
    }
    
    /**
     * Enhances the evidence string when reverting from REVIEWED to RAW.
     *
     * @param originalEvidence the original evidence
     * @param reason the reason for reverting the review
     * @return enhanced evidence string
     */
    private String enhanceEvidenceWithReversion(String originalEvidence, String reason) {
        StringBuilder enhancedEvidence = new StringBuilder();
        
        if (originalEvidence != null) {
            enhancedEvidence.append(originalEvidence).append("; ");
        }
        
        enhancedEvidence.append("Reverted from REVIEWED to RAW because: ")
                .append(reason != null ? reason : "unknown reason");
        
        return enhancedEvidence.toString();
    }
}