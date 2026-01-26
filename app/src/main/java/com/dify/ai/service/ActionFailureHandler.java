package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ActionFailureHandler creates action failure handling mechanism.
 * It manages what happens when actions fail during execution.
 */
@Service
public class ActionFailureHandler {

    /**
     * Handles an action failure and returns an updated FactsState reflecting the failure.
     *
     * @param action the action that failed
     * @param factsState the current facts state before the failure
     * @param failureReason the reason for the failure
     * @return an updated facts state that reflects the failure
     */
    public FactsState handleActionFailure(Action action, FactsState factsState, String failureReason) {
        // Log the failure
        System.out.println("Action failed: " + action.getActionId() + " - Reason: " + failureReason);
        
        // In a real implementation, we might want to:
        // 1. Record the failure in an audit trail
        // 2. Update the facts state to reflect the failure
        // 3. Potentially trigger a fallback action
        // 4. Notify relevant systems
        
        // For this implementation, we'll update the facts state with failure information
        FactsState updatedState = new FactsState();
        updatedState.getFacts().putAll(factsState.getFacts()); // Copy existing facts
        
        // Add a fact indicating the failure
        String failureFactId = "FAILURE_" + action.getActionId();
        updatedState.addFactValue(failureFactId,
            com.dify.ai.domain.model.FactValue.builder()
                .value(failureReason)
                .state(com.dify.ai.domain.model.FactValue.State.REVIEWED)
                .source("ACTION_FAILURE")
                .evidence("Action " + action.getActionId() + " failed with reason: " + failureReason)
                .build()
        );
        
        return updatedState;
    }

    /**
     * Determines if an action failure is recoverable.
     *
     * @param action the action that failed
     * @param failureReason the reason for the failure
     * @return true if the failure is recoverable, false otherwise
     */
    public boolean isRecoverableFailure(Action action, String failureReason) {
        // In a real implementation, this would have more sophisticated logic
        // to determine if a failure is recoverable based on the action type and failure reason
        
        // For now, we'll consider failures due to temporary issues as recoverable
        if (failureReason == null) {
            return false;
        }
        
        String lowerReason = failureReason.toLowerCase();
        return lowerReason.contains("timeout") || 
               lowerReason.contains("connection") || 
               lowerReason.contains("temporary") ||
               lowerReason.contains("retry");
    }

    /**
     * Gets a fallback action for when the primary action fails.
     *
     * @param failedAction the action that failed
     * @param failureReason the reason for the failure
     * @return a fallback action, or null if no fallback is available
     */
    public Action getFallbackAction(Action failedAction, String failureReason) {
        // In a real implementation, this would map specific failures to specific fallback actions
        // For this example, we'll return null indicating no fallback
        return null;
    }

    /**
     * Processes a sequence of actions, handling failures appropriately.
     *
     * @param actions the sequence of actions to execute
     * @param factsState the initial facts state
     * @return the final facts state after processing all actions (successful or failed)
     */
    public FactsState processActionSequenceWithFailureHandling(
            java.util.List<Action> actions, 
            FactsState factsState) {
        
        FactsState currentState = factsState;
        
        for (Action action : actions) {
            try {
                // Check if the action is eligible before executing
                ActionEligibilityChecker checker = new ActionEligibilityChecker();
                if (checker.isActionEligible(action, currentState)) {
                    // In a real implementation, we would call the actual action execution
                    // For this example, we'll simulate execution
                    currentState = simulateActionExecution(action, currentState);
                } else {
                    // Action is not eligible, treat as a failure
                    currentState = handleActionFailure(
                        action, 
                        currentState, 
                        "Action not eligible due to missing required facts"
                    );
                }
            } catch (Exception e) {
                // Handle unexpected exceptions during action execution
                currentState = handleActionFailure(action, currentState, e.getMessage());
            }
        }
        
        return currentState;
    }

    /**
     * Simulates action execution for the purpose of this example.
     * In a real implementation, this would call the actual business logic.
     *
     * @param action the action to execute
     * @param factsState the current facts state
     * @return the updated facts state after execution
     */
    private FactsState simulateActionExecution(Action action, FactsState factsState) {
        // This is a simplified simulation
        // In a real implementation, this would call the actual service methods
        
        // For this example, we'll randomly determine if the action succeeds or fails
        // In a real system, this would be determined by the actual business logic
        
        // Simulate success by updating the facts state with the action's produced facts
        FactsState newState = new FactsState();
        newState.getFacts().putAll(factsState.getFacts()); // Copy existing facts
        
        // Add facts that this action produces
        for (String producedFactId : action.getProduces()) {
            newState.addFactValue(producedFactId,
                com.dify.ai.domain.model.FactValue.builder()
                    .value("Simulated value for " + producedFactId)
                    .state(com.dify.ai.domain.model.FactValue.State.REVIEWED)
                    .source("SIMULATED_ACTION_EXECUTION")
                    .evidence("Simulated production by action: " + action.getActionId())
                    .build()
            );
        }
        
        return newState;
    }

    /**
     * Records action failure metrics for monitoring and analysis.
     *
     * @param actionId the ID of the failed action
     * @param failureReason the reason for the failure
     * @param durationMs the duration of the action before failure (in milliseconds)
     */
    public void recordFailureMetrics(String actionId, String failureReason, long durationMs) {
        // In a real implementation, this would record metrics to a monitoring system
        System.out.printf("Action Failure Metrics - Action: %s, Reason: %s, Duration: %d ms%n", 
                         actionId, failureReason, durationMs);
    }
}