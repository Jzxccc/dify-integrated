package com.dify.ai.rule;

import java.util.Map;

/**
 * Interface for atomic rule evaluators.
 * Each evaluator checks if a specific condition is met in the user input.
 */
public interface AtomicRuleEvaluator {
    
    /**
     * Evaluates the rule against the user input.
     *
     * @param userInput the raw user input to evaluate
     * @param params additional parameters for the rule evaluation
     * @return true if the rule condition is met, false otherwise
     */
    boolean evaluate(String userInput, Map<String, Object> params);
    
    /**
     * Gets the type of this rule evaluator.
     *
     * @return the rule type
     */
    String getType();
}