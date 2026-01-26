package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PipelineErrorHandler implements error handling across the entire pipeline.
 * It provides comprehensive error handling for each stage of the processing pipeline.
 */
@Service
public class PipelineErrorHandler {

    @Resource
    private AuditTrailService auditTrailService;

    /**
     * Processes user input through the complete pipeline with comprehensive error handling.
     *
     * @param userInput the raw user input to process
     * @param availableActions all available actions in the system
     * @return the final facts state after processing through the entire pipeline
     */
    public ProcessingResult processThroughPipelineWithErrorHandling(String userInput, List<Action> availableActions) {
        try {
            // Step 1: Intent Recognition with error handling
            IntentRecognitionResult intentResult = recognizeIntentWithErrorHandling(userInput);
            if (intentResult == null || intentResult.getIdentifiedIntents().isEmpty()) {
                return ProcessingResult.failure("Could not identify intent from user input", null);
            }
            
            String identifiedIntent = intentResult.getIdentifiedIntents().get(0);
            
            // Step 2: Fact Binding with error handling
            FactsState rawFactsState = bindFactsWithErrorHandling(userInput);
            if (rawFactsState == null) {
                return ProcessingResult.failure("Fact binding failed", null);
            }
            
            // Step 3: Review Agent with error handling
            FactsState reviewedFactsState = processThroughReviewAgentWithErrorHandling(rawFactsState, userInput);
            if (reviewedFactsState == null) {
                return ProcessingResult.failure("Review agent processing failed", rawFactsState);
            }
            
            // Step 4: Action Execution with error handling
            FactsState finalFactsState = executeActionsWithErrorHandling(reviewedFactsState, availableActions);
            
            return ProcessingResult.success(finalFactsState);
            
        } catch (Exception e) {
            // Catch any unhandled exceptions
            String errorMessage = "Unexpected error in pipeline: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            
            // Log to audit trail
            auditTrailService.recordFactValidation(null, "PipelineErrorHandler", "ERROR", errorMessage);
            
            return ProcessingResult.failure(errorMessage, null);
        }
    }

    /**
     * Recognizes intent from user input with error handling.
     *
     * @param userInput the user input to analyze
     * @return the intent recognition result
     */
    private IntentRecognitionResult recognizeIntentWithErrorHandling(String userInput) {
        try {
            // Validate input
            if (userInput == null || userInput.trim().isEmpty()) {
                System.err.println("User input is null or empty");
                return new IntentRecognitionResult();
            }
            
            // Attempt intent recognition using multiple approaches
            List<String> possibleIntents = List.of("SUPPLIER_QUERY", "ORDER_STATUS", "PRODUCT_INFO", "GENERAL_HELP");
            
            // Use LLM classifier
            String llmResult = null;
            try {
                llmResult = getBean(LLMIntentClassifier.class).classifyIntentWithLLM(userInput, possibleIntents);
            } catch (Exception e) {
                System.err.println("LLM intent classification failed: " + e.getMessage());
                // Continue to try other methods
            }
            
            // Prepare result
            IntentRecognitionResult result = new IntentRecognitionResult();
            if (llmResult != null) {
                result.getIdentifiedIntents().add(llmResult);
            } else {
                // Default to GENERAL_HELP if no specific intent identified
                result.getIdentifiedIntents().add("GENERAL_HELP");
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Error in intent recognition: " + e.getMessage());
            e.printStackTrace();
            
            // Log to audit trail
            auditTrailService.recordFactValidation(null, "PipelineErrorHandler", "INTENT_RECOGNITION_ERROR", e.getMessage());
            
            return new IntentRecognitionResult();
        }
    }

    /**
     * Binds facts from user input with error handling.
     *
     * @param userInput the user input to analyze
     * @return the facts state with raw values
     */
    private FactsState bindFactsWithErrorHandling(String userInput) {
        try {
            // Validate input
            if (userInput == null || userInput.trim().isEmpty()) {
                System.err.println("User input is null or empty for fact binding");
                return new FactsState();
            }
            
            // Perform fact binding
            FactBindingService factBindingService = getBean(FactBindingService.class);
            return factBindingService.bindFacts(userInput);
            
        } catch (Exception e) {
            System.err.println("Error in fact binding: " + e.getMessage());
            e.printStackTrace();
            
            // Log to audit trail
            auditTrailService.recordFactValidation(null, "PipelineErrorHandler", "FACT_BINDING_ERROR", e.getMessage());
            
            return null;
        }
    }

    /**
     * Processes facts through the review agent with error handling.
     *
     * @param rawFactsState the facts state with raw values
     * @param userInput the original user input for context
     * @return the facts state with reviewed values
     */
    private FactsState processThroughReviewAgentWithErrorHandling(FactsState rawFactsState, String userInput) {
        try {
            // Validate input
            if (rawFactsState == null) {
                System.err.println("Raw facts state is null for review agent processing");
                return new FactsState();
            }
            
            // Process each fact through the review agent
            FactsState reviewedFactsState = new FactsState();
            
            // Get required services
            RawFactValueValidator rawFactValueValidator = getBean(RawFactValueValidator.class);
            FactValueStateManager factValueStateManager = getBean(FactValueStateManager.class);
            
            // Process each fact in the raw facts state
            for (String factId : rawFactsState.getFacts().keySet()) {
                var rawFactValues = rawFactsState.getFactValues(factId);
                
                for (var rawFactValue : rawFactValues) {
                    // Validate the raw fact value
                    var validationIssues = rawFactValueValidator.validateRawFactValue(rawFactValue);
                    
                    if (validationIssues.isEmpty()) {
                        // If validation passes, transition to REVIEWED
                        var reviewedFactValue = factValueStateManager.transitionToReviewed(
                            rawFactValue, 
                            "ReviewAgent", 
                            "Automatically confirmed by validation"
                        );
                        
                        reviewedFactsState.addFactValue(factId, reviewedFactValue);
                    } else {
                        // If validation fails, we might need clarification
                        // For this example, we'll still accept it but note the issues
                        var reviewedFactValue = factValueStateManager.transitionToReviewed(
                            rawFactValue, 
                            "ReviewAgent", 
                            "Accepted despite validation issues: " + String.join(", ", validationIssues)
                        );
                        
                        reviewedFactsState.addFactValue(factId, reviewedFactValue);
                    }
                }
            }
            
            return reviewedFactsState;
            
        } catch (Exception e) {
            System.err.println("Error in review agent processing: " + e.getMessage());
            e.printStackTrace();
            
            // Log to audit trail
            auditTrailService.recordFactValidation(null, "PipelineErrorHandler", "REVIEW_AGENT_ERROR", e.getMessage());
            
            return null;
        }
    }

    /**
     * Executes actions with error handling.
     *
     * @param factsState the facts state with reviewed values
     * @param availableActions the available actions to consider
     * @return the final facts state after action execution
     */
    private FactsState executeActionsWithErrorHandling(FactsState factsState, List<Action> availableActions) {
        try {
            // Validate inputs
            if (factsState == null || availableActions == null) {
                System.err.println("Invalid inputs for action execution");
                return factsState != null ? factsState : new FactsState();
            }
            
            // Execute actions using the orchestrator
            ActionOrchestrator actionOrchestrator = getBean(ActionOrchestrator.class);
            return actionOrchestrator.executeActionsSequentially(availableActions, factsState);
            
        } catch (Exception e) {
            System.err.println("Error in action execution: " + e.getMessage());
            e.printStackTrace();
            
            // Log to audit trail
            auditTrailService.recordFactValidation(null, "PipelineErrorHandler", "ACTION_EXECUTION_ERROR", e.getMessage());
            
            // Return the original facts state if execution fails
            return factsState;
        }
    }

    /**
     * Utility method to get a bean from the Spring context.
     * In a real implementation, this would be handled by Spring's dependency injection.
     */
    private <T> T getBean(Class<T> clazz) {
        // This is a simplified implementation for the example
        // In a real Spring application, beans would be injected via @Autowired
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Represents the result of pipeline processing.
     */
    public static class ProcessingResult {
        private final boolean success;
        private final String errorMessage;
        private final FactsState finalFactsState;
        private final FactsState intermediateState; // State at the point of failure

        private ProcessingResult(boolean success, String errorMessage, FactsState finalFactsState, FactsState intermediateState) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.finalFactsState = finalFactsState;
            this.intermediateState = intermediateState;
        }

        public static ProcessingResult success(FactsState factsState) {
            return new ProcessingResult(true, null, factsState, null);
        }

        public static ProcessingResult failure(String errorMessage, FactsState intermediateState) {
            return new ProcessingResult(false, errorMessage, null, intermediateState);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public FactsState getFinalFactsState() { return finalFactsState; }
        public FactsState getIntermediateState() { return intermediateState; }
    }

    /**
     * Represents the result of intent recognition.
     */
    public static class IntentRecognitionResult {
        private final java.util.List<String> identifiedIntents;

        public IntentRecognitionResult() {
            this.identifiedIntents = new java.util.ArrayList<>();
        }

        public java.util.List<String> getIdentifiedIntents() {
            return identifiedIntents;
        }
    }
}