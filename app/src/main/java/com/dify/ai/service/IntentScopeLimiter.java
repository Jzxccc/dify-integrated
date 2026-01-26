package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IntentScopeLimiter implements the intent output mechanism to limit subsequent processing scope.
 * It ensures that only actions relevant to the identified intent are considered.
 */
@Service
public class IntentScopeLimiter {

    @Autowired
    private FactSlotRegistry factSlotRegistry;

    /**
     * Filters available actions based on the identified intent.
     * This limits the subsequent processing scope to only relevant actions.
     *
     * @param identifiedIntent the intent identified from user input
     * @param allAvailableActions all available actions in the system
     * @return a filtered list of actions relevant to the identified intent
     */
    public List<Action> filterActionsByIntent(String identifiedIntent, List<Action> allAvailableActions) {
        if (identifiedIntent == null || identifiedIntent.trim().isEmpty() || allAvailableActions == null) {
            return allAvailableActions; // Return all if no intent or null inputs
        }

        // In a real implementation, this would have a mapping of intents to allowed actions
        // For this example, we'll implement a simple mapping based on intent name
        Set<String> allowedActionTypes = getAllowedActionTypesForIntent(identifiedIntent);

        return allAvailableActions.stream()
                .filter(action -> allowedActionTypes.contains(getActionType(action.getActionId())))
                .collect(Collectors.toList());
    }

    /**
     * Determines which action types are allowed for a given intent.
     * This is where the intent-to-action mapping would be defined.
     *
     * @param intent the identified intent
     * @return a set of allowed action types for this intent
     */
    private Set<String> getAllowedActionTypesForIntent(String intent) {
        switch (intent.toUpperCase()) {
            case "SUPPLIER_QUERY":
                // For supplier queries, allow actions related to supplier information
                return Set.of("SUPPLIER", "CONTACT", "INFORMATION");
            case "ORDER_STATUS":
                // For order status queries, allow actions related to orders
                return Set.of("ORDER", "TRACKING", "STATUS");
            case "PRODUCT_INFO":
                // For product info queries, allow actions related to products
                return Set.of("PRODUCT", "CATALOG", "PRICING");
            case "GENERAL_HELP":
                // For general help, allow general assistance actions
                return Set.of("HELP", "SUPPORT", "GUIDANCE");
            default:
                // For unknown intents, allow general actions
                return Set.of("GENERAL", "DEFAULT");
        }
    }

    /**
     * Extracts the action type from an action ID.
     * This is a simple implementation assuming action IDs follow a pattern like "TYPE_operation".
     *
     * @param actionId the action ID
     * @return the action type
     */
    private String getActionType(String actionId) {
        if (actionId == null || !actionId.contains("_")) {
            return "GENERAL"; // Default type
        }
        
        // Extract the prefix before the first underscore
        return actionId.substring(0, actionId.indexOf('_')).toUpperCase();
    }

    /**
     * Limits the set of FactSlots that should be considered based on the identified intent.
     * This narrows down the fact extraction process to only relevant facts.
     *
     * @param identifiedIntent the identified intent
     * @return a set of FactSlot IDs that are relevant to the intent
     */
    public Set<String> getRelevantFactSlotsForIntent(String identifiedIntent) {
        if (identifiedIntent == null || identifiedIntent.trim().isEmpty()) {
            // If no intent is identified, return all fact slots
            return factSlotRegistry.getAllFactSlots().stream()
                    .map(slot -> slot.getFactId())
                    .collect(Collectors.toSet());
        }

        // Return fact slots relevant to the identified intent
        switch (identifiedIntent.toUpperCase()) {
            case "SUPPLIER_QUERY":
                return Set.of("SUPPLIER_NAME", "CONTACT_INFO", "SERVICE_TYPE");
            case "ORDER_STATUS":
                return Set.of("ORDER_ID", "CUSTOMER_ID", "DATE_RANGE");
            case "PRODUCT_INFO":
                return Set.of("PRODUCT_ID", "CATEGORY", "PRICE_RANGE");
            case "GENERAL_HELP":
                return Set.of("QUERY_TEXT", "PRIORITY_LEVEL");
            default:
                // For unknown intents, return commonly used fact slots
                return Set.of("QUERY_TEXT", "CONTEXT_INFO");
        }
    }
}