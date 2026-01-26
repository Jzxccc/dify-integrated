package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PipelineMonitoringService adds logging and monitoring for each stage of the pipeline.
 * It provides comprehensive monitoring capabilities for the processing pipeline.
 */
@Service
public class PipelineMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(PipelineMonitoringService.class);

    @Resource
    private AuditTrailService auditTrailService;

    /**
     * Processes user input through the complete pipeline with comprehensive logging and monitoring.
     *
     * @param userInput the raw user input to process
     * @param availableActions all available actions in the system
     * @return the final facts state after processing through the entire pipeline
     */
    public ProcessingResult processThroughPipelineWithMonitoring(String userInput, List<Action> availableActions) {
        long startTime = System.currentTimeMillis();
        String requestId = generateRequestId();

        logger.info("Pipeline started - Request ID: {}, Input: {}", requestId, maskSensitiveData(userInput));

        try {
            // Log input validation
            if (userInput == null || userInput.trim().isEmpty()) {
                String errorMsg = "User input is null or empty";
                logger.warn("Input validation failed - Request ID: {}, Error: {}", requestId, errorMsg);
                return ProcessingResult.failure(errorMsg, null);
            }

            // Step 1: Intent Recognition with monitoring
            MonitoringResult<IntentRecognitionResult> intentResult = 
                monitorIntentRecognition(requestId, userInput);
            if (!intentResult.isSuccess() || intentResult.getResult() == null || 
                intentResult.getResult().getIdentifiedIntents().isEmpty()) {
                String errorMsg = "Could not identify intent from user input";
                logger.error("Intent recognition failed - Request ID: {}, Error: {}", requestId, errorMsg);
                return ProcessingResult.failure(errorMsg, null);
            }

            String identifiedIntent = intentResult.getResult().getIdentifiedIntents().get(0);
            logger.debug("Intent recognized - Request ID: {}, Intent: {}", requestId, identifiedIntent);

            // Step 2: Fact Binding with monitoring
            MonitoringResult<FactsState> rawFactsResult = 
                monitorFactBinding(requestId, userInput);
            if (!rawFactsResult.isSuccess() || rawFactsResult.getResult() == null) {
                String errorMsg = "Fact binding failed";
                logger.error("Fact binding failed - Request ID: {}, Error: {}", requestId, errorMsg);
                return ProcessingResult.failure(errorMsg, null);
            }

            FactsState rawFactsState = rawFactsResult.getResult();
            logger.debug("Fact binding completed - Request ID: {}, Facts count: {}", 
                        requestId, rawFactsState.getFacts().size());

            // Step 3: Review Agent with monitoring
            MonitoringResult<FactsState> reviewedFactsResult = 
                monitorReviewAgent(requestId, rawFactsState, userInput);
            if (!reviewedFactsResult.isSuccess() || reviewedFactsResult.getResult() == null) {
                String errorMsg = "Review agent processing failed";
                logger.error("Review agent processing failed - Request ID: {}, Error: {}", requestId, errorMsg);
                return ProcessingResult.failure(errorMsg, rawFactsState);
            }

            FactsState reviewedFactsState = reviewedFactsResult.getResult();
            logger.debug("Review agent completed - Request ID: {}, Reviewed facts count: {}", 
                        requestId, countReviewedFacts(reviewedFactsState));

            // Step 4: Action Execution with monitoring
            MonitoringResult<FactsState> actionExecutionResult = 
                monitorActionExecution(requestId, reviewedFactsState, availableActions);

            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("Pipeline completed successfully - Request ID: {}, Total time: {}ms", 
                       requestId, totalTime);

            return ProcessingResult.success(actionExecutionResult.getResult());

        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            String errorMsg = "Unexpected error in pipeline: " + e.getMessage();
            logger.error("Pipeline failed - Request ID: {}, Error: {}, Total time: {}ms", 
                        requestId, errorMsg, totalTime);
            e.printStackTrace();

            return ProcessingResult.failure(errorMsg, null);
        }
    }

    /**
     * Monitors the intent recognition stage.
     */
    private MonitoringResult<IntentRecognitionResult> monitorIntentRecognition(String requestId, String userInput) {
        long startTime = System.currentTimeMillis();
        logger.debug("Starting intent recognition - Request ID: {}", requestId);

        try {
            // Attempt intent recognition using multiple approaches
            List<String> possibleIntents = List.of("SUPPLIER_QUERY", "ORDER_STATUS", "PRODUCT_INFO", "GENERAL_HELP");

            // Use LLM classifier
            String llmResult = null;
            try {
                LLMIntentClassifier llmIntentClassifier = getBean(LLMIntentClassifier.class);
                llmResult = llmIntentClassifier.classifyIntentWithLLM(userInput, possibleIntents);
            } catch (Exception e) {
                logger.warn("LLM intent classification failed - Request ID: {}, Error: {}", requestId, e.getMessage());
            }

            // Prepare result
            IntentRecognitionResult result = new IntentRecognitionResult();
            if (llmResult != null) {
                result.getIdentifiedIntents().add(llmResult);
                logger.debug("LLM intent classification succeeded - Request ID: {}, Result: {}", requestId, llmResult);
            } else {
                // Default to GENERAL_HELP if no specific intent identified
                result.getIdentifiedIntents().add("GENERAL_HELP");
                logger.debug("Using default intent - Request ID: {}", requestId);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Intent recognition completed - Request ID: {}, Duration: {}ms", requestId, duration);

            return MonitoringResult.success(result, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Intent recognition failed - Request ID: {}, Error: {}, Duration: {}ms", 
                        requestId, e.getMessage(), duration);
            return MonitoringResult.failure(e.getMessage(), duration);
        }
    }

    /**
     * Monitors the fact binding stage.
     */
    private MonitoringResult<FactsState> monitorFactBinding(String requestId, String userInput) {
        long startTime = System.currentTimeMillis();
        logger.debug("Starting fact binding - Request ID: {}", requestId);

        try {
            // Perform fact binding
            FactBindingService factBindingService = getBean(FactBindingService.class);
            FactsState result = factBindingService.bindFacts(userInput);

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Fact binding completed - Request ID: {}, Duration: {}ms, Facts count: {}", 
                        requestId, duration, result.getFacts().size());

            return MonitoringResult.success(result, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Fact binding failed - Request ID: {}, Error: {}, Duration: {}ms", 
                        requestId, e.getMessage(), duration);
            return MonitoringResult.failure(e.getMessage(), duration);
        }
    }

    /**
     * Monitors the review agent stage.
     */
    private MonitoringResult<FactsState> monitorReviewAgent(String requestId, FactsState rawFactsState, String userInput) {
        long startTime = System.currentTimeMillis();
        logger.debug("Starting review agent processing - Request ID: {}", requestId);

        try {
            // Process each fact through the review agent
            FactsState reviewedFactsState = new FactsState();

            // Get required services
            RawFactValueValidator rawFactValueValidator = getBean(RawFactValueValidator.class);
            FactValueStateManager factValueStateManager = getBean(FactValueStateManager.class);

            int processedCount = 0;
            int validatedCount = 0;
            int errorCount = 0;

            // Process each fact in the raw facts state
            for (String factId : rawFactsState.getFacts().keySet()) {
                var rawFactValues = rawFactsState.getFactValues(factId);

                for (var rawFactValue : rawFactValues) {
                    processedCount++;

                    try {
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
                            validatedCount++;
                        } else {
                            // If validation fails, we might need clarification
                            // For this example, we'll still accept it but note the issues
                            var reviewedFactValue = factValueStateManager.transitionToReviewed(
                                rawFactValue,
                                "ReviewAgent",
                                "Accepted despite validation issues: " + String.join(", ", validationIssues)
                            );

                            reviewedFactsState.addFactValue(factId, reviewedFactValue);
                            errorCount++;
                        }
                    } catch (Exception e) {
                        logger.warn("Error validating fact - Request ID: {}, Fact ID: {}, Error: {}", 
                                   requestId, factId, e.getMessage());
                        errorCount++;
                    }
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Review agent completed - Request ID: {}, Duration: {}ms, Processed: {}, Validated: {}, Errors: {}", 
                        requestId, duration, processedCount, validatedCount, errorCount);

            return MonitoringResult.success(reviewedFactsState, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Review agent processing failed - Request ID: {}, Error: {}, Duration: {}ms", 
                        requestId, e.getMessage(), duration);
            return MonitoringResult.failure(e.getMessage(), duration);
        }
    }

    /**
     * Monitors the action execution stage.
     */
    private MonitoringResult<FactsState> monitorActionExecution(String requestId, FactsState factsState, List<Action> availableActions) {
        long startTime = System.currentTimeMillis();
        logger.debug("Starting action execution - Request ID: {}", requestId);

        try {
            // Execute actions using the orchestrator
            ActionOrchestrator actionOrchestrator = getBean(ActionOrchestrator.class);
            FactsState result = actionOrchestrator.executeActionsSequentially(availableActions, factsState);

            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Action execution completed - Request ID: {}, Duration: {}ms", requestId, duration);

            return MonitoringResult.success(result, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Action execution failed - Request ID: {}, Error: {}, Duration: {}ms", 
                        requestId, e.getMessage(), duration);
            return MonitoringResult.failure(e.getMessage(), duration);
        }
    }

    /**
     * Counts the number of REVIEWED facts in a facts state.
     */
    private int countReviewedFacts(FactsState factsState) {
        int count = 0;
        for (String factId : factsState.getFacts().keySet()) {
            var factValues = factsState.getFactValues(factId);
            for (var factValue : factValues) {
                if (factValue.getState() == com.dify.ai.domain.model.FactValue.State.REVIEWED) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Generates a unique request ID for monitoring purposes.
     */
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }

    /**
     * Masks sensitive data in logs.
     */
    private String maskSensitiveData(String input) {
        // In a real implementation, you would have more sophisticated data masking
        if (input == null) return null;
        // For now, just return the first 50 characters to avoid logging very long inputs
        return input.length() > 50 ? input.substring(0, 50) + "..." : input;
    }

    /**
     * Utility method to get a bean from the Spring context.
     */
    private <T> T getBean(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Represents the result of a monitored operation.
     */
    public static class MonitoringResult<T> {
        private final boolean success;
        private final String errorMessage;
        private final T result;
        private final long durationMs;

        private MonitoringResult(boolean success, String errorMessage, T result, long durationMs) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.result = result;
            this.durationMs = durationMs;
        }

        public static <T> MonitoringResult<T> success(T result, long durationMs) {
            return new MonitoringResult<>(true, null, result, durationMs);
        }

        public static <T> MonitoringResult<T> failure(String errorMessage, long durationMs) {
            return new MonitoringResult<>(false, errorMessage, null, durationMs);
        }

        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        public T getResult() { return result; }
        public long getDurationMs() { return durationMs; }
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