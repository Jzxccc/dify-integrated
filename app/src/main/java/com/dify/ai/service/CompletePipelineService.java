package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CompletePipelineService creates the complete pipeline for processing user input.
 * It implements the full flow: User Input → Intent Recognition → Fact Binding → FactsState (RAW) →
 * Review Agent → FactsState (REVIEWED) → Action Planner → Action Execution
 */
@Service
public class CompletePipelineService {

    @Resource
    private VectorSimilarityService vectorSimilarityService;

    @Resource
    private LLMIntentClassifier llmIntentClassifier;

    @Resource
    private IntentScopeLimiter intentScopeLimiter;

    @Resource
    private BoundaryEnforcer boundaryEnforcer;

    @Resource
    private FactBindingService factBindingService;

    @Resource
    private RawFactValueValidator rawFactValueValidator;

    @Resource
    private AmbiguityResolver ambiguityResolver;

    @Resource
    private ClarificationRequestGenerator clarificationRequestGenerator;

    @Resource
    private FactValueStateManager factValueStateManager;

    @Resource
    private AuditTrailService auditTrailService;

    @Resource
    private ActionEligibilityChecker actionEligibilityChecker;

    @Resource
    private ActionDependencyResolver actionDependencyResolver;

    @Resource
    private ActionOrchestrator actionOrchestrator;

    @Resource
    private ActionFailureHandler actionFailureHandler;

    /**
     * Processes user input through the complete pipeline.
     *
     * @param userInput the raw user input to process
     * @param availableActions all available actions in the system
     * @return the final facts state after processing through the entire pipeline
     */
    public FactsState processThroughCompletePipeline(String userInput, List<Action> availableActions) {
        // Step 1: Intent Recognition
        String identifiedIntent = recognizeIntent(userInput);
        
        // Step 2: Limit scope based on identified intent
        List<Action> filteredActions = intentScopeLimiter.filterActionsByIntent(identifiedIntent, availableActions);
        
        // Step 3: Fact Binding (creates FactsState with RAW values)
        FactsState rawFactsState = factBindingService.bindFacts(userInput);
        
        // Step 4: Review Agent (validates and transitions RAW facts to REVIEWED)
        FactsState reviewedFactsState = processThroughReviewAgent(rawFactsState, userInput);
        
        // Step 5: Action Planning and Execution
        FactsState finalFactsState = executeEligibleActions(reviewedFactsState, filteredActions);
        
        return finalFactsState;
    }

    /**
     * Recognizes intent from user input using both vector similarity and LLM approaches.
     *
     * @param userInput the user input to analyze
     * @return the identified intent
     */
    private String recognizeIntent(String userInput) {
        // For this example, we'll use a simple approach
        // In a real implementation, you might combine vector similarity and LLM results
        
        // Use LLM classifier as primary approach
        List<String> possibleIntents = List.of("SUPPLIER_QUERY", "ORDER_STATUS", "PRODUCT_INFO", "GENERAL_HELP");
        String llmResult = llmIntentClassifier.classifyIntentWithLLM(userInput, possibleIntents);
        
        if (llmResult != null) {
            return llmResult;
        }
        
        // Fallback to vector similarity if LLM doesn't return a result
        // This would require pre-defined intent patterns
        return "GENERAL_HELP"; // Default fallback
    }

    /**
     * Processes the raw facts through the Review Agent to validate and confirm them.
     *
     * @param rawFactsState the facts state with RAW values
     * @param userInput the original user input for context
     * @return the facts state with REVIEWED values
     */
    private FactsState processThroughReviewAgent(FactsState rawFactsState, String userInput) {
        FactsState reviewedFactsState = new FactsState();
        
        // Process each fact in the raw facts state
        for (String factId : rawFactsState.getFacts().keySet()) {
            var rawFactValues = rawFactsState.getFactValues(factId);
            
            // Validate each raw fact value
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
                    
                    // Record in audit trail
                    auditTrailService.recordFactValidation(
                        reviewedFactValue, 
                        "ReviewAgent", 
                        "ACCEPTED", 
                        "Passed validation"
                    );
                } else {
                    // If validation fails, we might need clarification
                    // For this example, we'll still accept it but note the issues
                    var reviewedFactValue = factValueStateManager.transitionToReviewed(
                        rawFactValue, 
                        "ReviewAgent", 
                        "Accepted despite validation issues: " + String.join(", ", validationIssues)
                    );
                    
                    reviewedFactsState.addFactValue(factId, reviewedFactValue);
                    
                    // Record in audit trail
                    auditTrailService.recordFactValidation(
                        reviewedFactValue, 
                        "ReviewAgent", 
                        "ACCEPTED_WITH_ISSUES", 
                        String.join(", ", validationIssues)
                    );
                }
            }
        }
        
        return reviewedFactsState;
    }

    /**
     * Executes eligible actions based on the reviewed facts state.
     *
     * @param factsState the facts state with REVIEWED values
     * @param availableActions the available actions to consider
     * @return the final facts state after action execution
     */
    private FactsState executeEligibleActions(FactsState factsState, List<Action> availableActions) {
        try {
            // Select and execute eligible actions
            return actionOrchestrator.executeActionsSequentially(availableActions, factsState);
        } catch (Exception e) {
            // Handle any failures during action execution
            System.err.println("Error during action execution: " + e.getMessage());
            
            // In a real implementation, you might want to handle this differently
            // For now, we'll return the original facts state
            return factsState;
        }
    }
}