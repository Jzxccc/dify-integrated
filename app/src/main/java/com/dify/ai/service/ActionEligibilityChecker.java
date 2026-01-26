package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * ActionEligibilityChecker implements the action eligibility checking based on FactsState.
 * It determines if an action can be executed based on the required facts being present in REVIEWED state.
 */
@Service
public class ActionEligibilityChecker {

    /**
     * Checks if an action is eligible for execution based on the current FactsState.
     * An action is eligible if all its required facts are present in REVIEWED state.
     *
     * @param action the action to check
     * @param factsState the current facts state
     * @return true if the action is eligible for execution, false otherwise
     */
    public boolean isActionEligible(Action action, FactsState factsState) {
        if (action == null || factsState == null) {
            return false;
        }

        // Check if all required facts are present in REVIEWED state
        for (String requiredFactId : action.getRequires()) {
            if (!factsState.hasReviewedFact(requiredFactId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the set of missing required facts for an action.
     *
     * @param action the action to check
     * @param factsState the current facts state
     * @return a set of fact IDs that are required but missing from the FactsState
     */
    public Set<String> getMissingRequiredFacts(Action action, FactsState factsState) {
        Set<String> missingFacts = new HashSet<>();

        if (action == null || factsState == null) {
            return missingFacts;
        }

        for (String requiredFactId : action.getRequires()) {
            if (!factsState.hasReviewedFact(requiredFactId)) {
                missingFacts.add(requiredFactId);
            }
        }

        return missingFacts;
    }

    /**
     * Checks if an action is partially eligible (some but not all required facts are present).
     *
     * @param action the action to check
     * @param factsState the current facts state
     * @return true if some required facts are present but not all, false otherwise
     */
    public boolean isActionPartiallyEligible(Action action, FactsState factsState) {
        if (action == null || factsState == null) {
            return false;
        }

        boolean hasSomeRequiredFacts = false;
        boolean hasAllRequiredFacts = true;

        for (String requiredFactId : action.getRequires()) {
            if (factsState.hasReviewedFact(requiredFactId)) {
                hasSomeRequiredFacts = true;
            } else {
                hasAllRequiredFacts = false;
            }
        }

        return hasSomeRequiredFacts && !hasAllRequiredFacts;
    }

    /**
     * Gets the set of facts that are both required by the action and present in the FactsState.
     *
     * @param action the action to check
     * @param factsState the current facts state
     * @return a set of fact IDs that are both required and present
     */
    public Set<String> getPresentRequiredFacts(Action action, FactsState factsState) {
        Set<String> presentFacts = new HashSet<>();

        if (action == null || factsState == null) {
            return presentFacts;
        }

        for (String requiredFactId : action.getRequires()) {
            if (factsState.hasReviewedFact(requiredFactId)) {
                presentFacts.add(requiredFactId);
            }
        }

        return presentFacts;
    }
}