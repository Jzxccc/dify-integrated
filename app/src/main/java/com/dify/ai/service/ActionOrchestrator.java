package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ActionOrchestrator implements the action execution orchestrator.
 * It manages the execution of eligible actions based on the current FactsState.
 */
@Service
public class ActionOrchestrator {

    @Autowired
    private ActionEligibilityChecker actionEligibilityChecker;

    @Autowired
    private ActionDependencyResolver actionDependencyResolver;

    /**
     * Executes all eligible actions based on the current FactsState.
     *
     * @param eligibleActions the list of actions that are eligible for execution
     * @param factsState the current facts state
     * @return the updated facts state after executing the actions
     */
    public FactsState executeEligibleActions(List<Action> eligibleActions, FactsState factsState) {
        if (eligibleActions == null || eligibleActions.isEmpty() || factsState == null) {
            return factsState;
        }

        // Order actions based on dependencies to ensure correct execution order
        List<Action> orderedActions = actionDependencyResolver.orderActionsByDependencies(eligibleActions);

        FactsState currentState = factsState;

        // Execute each action in the proper order
        for (Action action : orderedActions) {
            if (actionEligibilityChecker.isActionEligible(action, currentState)) {
                currentState = executeAction(action, currentState);
            }
        }

        return currentState;
    }

    /**
     * Executes a single action and updates the FactsState.
     *
     * @param action the action to execute
     * @param factsState the current facts state
     * @return the updated facts state after executing the action
     */
    private FactsState executeAction(Action action, FactsState factsState) {
        // In a real implementation, this would call the actual business logic for the action
        // For this example, we'll simulate the action execution and update the facts state
        
        System.out.println("Executing action: " + action.getName() + " (" + action.getActionId() + ")");
        
        // Simulate action execution
        simulateActionExecution(action);
        
        // Update the FactsState with any facts produced by the action
        FactsState updatedState = new FactsState();
        updatedState.getFacts().putAll(factsState.getFacts()); // Copy existing facts
        
        // Add any facts produced by this action
        for (String producedFactId : action.getProduces()) {
            // In a real implementation, the action would produce actual fact values
            // For simulation, we'll create a dummy fact value
            Object producedValue = createProducedFactValue(action, producedFactId);
            
            // Add the produced fact to the state as a REVIEWED value
            updatedState.addFactValue(producedFactId, 
                com.dify.ai.domain.model.FactValue.builder()
                    .value(producedValue)
                    .state(com.dify.ai.domain.model.FactValue.State.REVIEWED)
                    .source("ACTION_EXECUTION")
                    .evidence("Produced by action: " + action.getActionId())
                    .build()
            );
        }
        
        return updatedState;
    }

    /**
     * Simulates the execution of an action.
     * In a real implementation, this would call the actual business logic.
     *
     * @param action the action to simulate execution for
     */
    private void simulateActionExecution(Action action) {
        // In a real implementation, this would call the actual service methods for the action
        // For example: if actionId is "GET_SUPPLIER_INFO", call supplierService.getInfo()
        
        // For this simulation, we'll just log the action execution
        System.out.println("Simulating execution of action: " + action.getActionId());
    }

    /**
     * Creates a fact value that is produced by an action.
     * In a real implementation, this would be the actual result of the action.
     *
     * @param action the action that produces the fact
     * @param producedFactId the ID of the fact being produced
     * @return the produced fact value
     */
    private Object createProducedFactValue(Action action, String producedFactId) {
        // In a real implementation, this would be the actual result of executing the action
        // For this simulation, we'll return a placeholder value
        return "Value produced by action " + action.getActionId() + " for fact " + producedFactId;
    }

    /**
     * Selects eligible actions from a pool of available actions based on the current FactsState.
     *
     * @param allAvailableActions all available actions in the system
     * @param factsState the current facts state
     * @return a list of actions that are eligible for execution
     */
    public List<Action> selectEligibleActions(List<Action> allAvailableActions, FactsState factsState) {
        return allAvailableActions.stream()
                .filter(action -> actionEligibilityChecker.isActionEligible(action, factsState))
                .toList();
    }

    /**
     * Executes actions in a sequential manner, updating the FactsState after each execution.
     * This is useful when actions need to be executed one by one and their results affect subsequent actions.
     *
     * @param allAvailableActions all available actions in the system
     * @param initialFactsState the initial facts state
     * @return the final facts state after executing all possible actions
     */
    public FactsState executeActionsSequentially(List<Action> allAvailableActions, FactsState initialFactsState) {
        FactsState currentState = initialFactsState;
        
        // Keep executing actions as long as new ones become eligible
        boolean newActionsExecuted;
        do {
            newActionsExecuted = false;
            
            // Select currently eligible actions
            List<Action> eligibleActions = selectEligibleActions(allAvailableActions, currentState);
            
            // Execute eligible actions
            if (!eligibleActions.isEmpty()) {
                currentState = executeEligibleActions(eligibleActions, currentState);
                newActionsExecuted = true;
            }
        } while (newActionsExecuted); // Continue until no new actions can be executed
        
        return currentState;
    }
}