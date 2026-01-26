package com.dify.ai.service;

import org.springframework.stereotype.Service;

/**
 * BoundaryEnforcer implements boundary enforcement to prevent direct fact generation.
 * It ensures that Intent Recognition only identifies intents without generating facts or action parameters.
 */
@Service
public class BoundaryEnforcer {

    /**
     * Validates that the intent recognition result only contains intent information
     * and does not contain any fact values or action parameters.
     *
     * @param intentRecognitionResult the result from intent recognition
     * @return true if the result respects the boundaries, false otherwise
     */
    public boolean validateIntentRecognitionBoundaries(IntentRecognitionResult intentRecognitionResult) {
        if (intentRecognitionResult == null) {
            return true; // Nothing to validate
        }

        // Check that no fact values are directly generated
        if (intentRecognitionResult.getDirectlyGeneratedFactValues() != null &&
            !intentRecognitionResult.getDirectlyGeneratedFactValues().isEmpty()) {
            return false;
        }

        // Check that no action parameters are directly determined
        if (intentRecognitionResult.getDirectlyDeterminedActionParameters() != null &&
            !intentRecognitionResult.getDirectlyDeterminedActionParameters().isEmpty()) {
            return false;
        }

        // Check that only intent information is present
        return intentRecognitionResult.getIdentifiedIntents() != null &&
               !intentRecognitionResult.getIdentifiedIntents().isEmpty();
    }

    /**
     * Enforces boundaries by cleaning up a result that violates them.
     *
     * @param result the potentially violating result
     * @return a cleaned result that respects boundaries
     */
    public IntentRecognitionResult enforceBoundaries(IntentRecognitionResult result) {
        if (result == null) {
            return null;
        }

        // Create a new result without the violating elements
        IntentRecognitionResult cleanResult = new IntentRecognitionResult();
        cleanResult.setIdentifiedIntents(result.getIdentifiedIntents());
        cleanResult.setConfidenceScores(result.getConfidenceScores());
        cleanResult.setProcessingMetadata(result.getProcessingMetadata());

        // Explicitly clear any fact values or action parameters that shouldn't be there
        cleanResult.setDirectlyGeneratedFactValues(null);
        cleanResult.setDirectlyDeterminedActionParameters(null);

        return cleanResult;
    }

    /**
     * Checks if a specific intent recognition approach respects boundaries.
     *
     * @param approach the approach being used for intent recognition
     * @return true if the approach respects boundaries, false otherwise
     */
    public boolean approachRespectsBoundaries(IntentRecognitionApproach approach) {
        if (approach == null) {
            return true;
        }

        // The approach should only focus on intent classification, not fact extraction or action parameter determination
        return approach.isIntentOnly() && 
               !approach.isGeneratingFacts() && 
               !approach.isDeterminingActionParameters();
    }

    /**
     * Represents the result of intent recognition.
     */
    public static class IntentRecognitionResult {
        private java.util.List<String> identifiedIntents;
        private java.util.Map<String, Double> confidenceScores;
        private java.util.Map<String, Object> processingMetadata;
        // These fields should NOT be populated by intent recognition (boundary violation)
        private java.util.Map<String, Object> directlyGeneratedFactValues;
        private java.util.Map<String, Object> directlyDeterminedActionParameters;

        // Getters and setters
        public java.util.List<String> getIdentifiedIntents() { return identifiedIntents; }
        public void setIdentifiedIntents(java.util.List<String> identifiedIntents) { this.identifiedIntents = identifiedIntents; }

        public java.util.Map<String, Double> getConfidenceScores() { return confidenceScores; }
        public void setConfidenceScores(java.util.Map<String, Double> confidenceScores) { this.confidenceScores = confidenceScores; }

        public java.util.Map<String, Object> getProcessingMetadata() { return processingMetadata; }
        public void setProcessingMetadata(java.util.Map<String, Object> processingMetadata) { this.processingMetadata = processingMetadata; }

        public java.util.Map<String, Object> getDirectlyGeneratedFactValues() { return directlyGeneratedFactValues; }
        public void setDirectlyGeneratedFactValues(java.util.Map<String, Object> directlyGeneratedFactValues) { this.directlyGeneratedFactValues = directlyGeneratedFactValues; }

        public java.util.Map<String, Object> getDirectlyDeterminedActionParameters() { return directlyDeterminedActionParameters; }
        public void setDirectlyDeterminedActionParameters(java.util.Map<String, Object> directlyDeterminedActionParameters) { this.directlyDeterminedActionParameters = directlyDeterminedActionParameters; }
    }

    /**
     * Represents an intent recognition approach and its characteristics.
     */
    public static class IntentRecognitionApproach {
        private boolean intentOnly;
        private boolean generatingFacts;
        private boolean determiningActionParameters;

        public IntentRecognitionApproach(boolean intentOnly, boolean generatingFacts, boolean determiningActionParameters) {
            this.intentOnly = intentOnly;
            this.generatingFacts = generatingFacts;
            this.determiningActionParameters = determiningActionParameters;
        }

        public boolean isIntentOnly() { return intentOnly; }
        public boolean isGeneratingFacts() { return generatingFacts; }
        public boolean isDeterminingActionParameters() { return determiningActionParameters; }
    }
}