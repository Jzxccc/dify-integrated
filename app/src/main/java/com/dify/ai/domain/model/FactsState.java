package com.dify.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FactsState represents the current "world state" for a request.
 * It contains a mapping from factId to a list of FactValues.
 * All actions are executed based on the state of FactsState.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactsState {

    /**
     * Mapping from factId to a list of FactValues
     * Each factId can have multiple FactValues (when multiValue is true)
     */
    @Builder.Default
    private Map<String, List<FactValue>> facts = new HashMap<>();

    /**
     * Get all FactValues for a given factId
     *
     * @param factId the fact identifier
     * @return list of FactValues for the given factId, or empty list if none exist
     */
    public List<FactValue> getFactValues(String factId) {
        return facts.getOrDefault(factId, List.of());
    }

    /**
     * Get the first FactValue for a given factId (for single-value facts)
     *
     * @param factId the fact identifier
     * @return the first FactValue for the given factId, or null if none exist
     */
    public FactValue getFirstFactValue(String factId) {
        List<FactValue> factValues = getFactValues(factId);
        return factValues.isEmpty() ? null : factValues.get(0);
    }

    /**
     * Add a FactValue for a given factId
     *
     * @param factId the fact identifier
     * @param factValue the FactValue to add
     */
    public void addFactValue(String factId, FactValue factValue) {
        facts.computeIfAbsent(factId, k -> new java.util.ArrayList<>()).add(factValue);
    }

    /**
     * Check if a fact with the given ID exists in REVIEWED state
     *
     * @param factId the fact identifier
     * @return true if the fact exists and has at least one REVIEWED value, false otherwise
     */
    public boolean hasReviewedFact(String factId) {
        return getFactValues(factId).stream()
                .anyMatch(fv -> fv.getState() == FactValue.State.REVIEWED);
    }
}