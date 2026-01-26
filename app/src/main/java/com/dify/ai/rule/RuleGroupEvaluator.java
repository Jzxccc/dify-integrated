package com.dify.ai.rule;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * RuleGroup evaluator that combines multiple atomic rules with OR/AND logic.
 * Implements the OR/AND combination logic as specified in the design:
 * - Multiple RuleGroups are evaluated with OR logic
 * - Multiple AtomicRules within a group are evaluated with AND logic
 */
@Component
public class RuleGroupEvaluator {

    @Resource
    private List<AtomicRuleEvaluator> ruleEvaluators;
    
    /**
     * Evaluates a group of rules using AND logic (all rules must pass).
     *
     * @param userInput the raw user input to evaluate
     * @param atomicRules list of atomic rules to evaluate
     * @return true if all rules in the group pass, false otherwise
     */
    public boolean evaluateRuleGroup(String userInput, List<AtomicRule> atomicRules) {
        // Evaluate all rules in the group with AND logic
        for (AtomicRule atomicRule : atomicRules) {
            AtomicRuleEvaluator evaluator = getRuleEvaluatorByType(atomicRule.getType());
            if (evaluator == null) {
                // If we can't find an evaluator for this rule type, the rule fails
                return false;
            }
            
            boolean result = evaluator.evaluate(userInput, atomicRule.getParams());
            if (!result) {
                // If any rule in the group fails, the whole group fails (AND logic)
                return false;
            }
        }
        
        // All rules in the group passed
        return true;
    }
    
    /**
     * Evaluates multiple rule groups using OR logic (any group can pass).
     *
     * @param userInput the raw user input to evaluate
     * @param ruleGroups list of rule groups to evaluate
     * @return true if any rule group passes, false otherwise
     */
    public boolean evaluateRuleGroups(String userInput, List<RuleGroup> ruleGroups) {
        // If there are no rule groups, return false (no conditions to satisfy)
        if (ruleGroups == null || ruleGroups.isEmpty()) {
            return false;
        }
        
        // Evaluate each rule group with OR logic
        for (RuleGroup ruleGroup : ruleGroups) {
            boolean groupResult = evaluateRuleGroup(userInput, ruleGroup.getAtomicRules());
            if (groupResult) {
                // If any group passes, the overall result is true (OR logic)
                return true;
            }
        }
        
        // No group passed
        return false;
    }
    
    /**
     * Gets the appropriate rule evaluator by its type.
     *
     * @param type the type of rule evaluator to get
     * @return the rule evaluator, or null if not found
     */
    private AtomicRuleEvaluator getRuleEvaluatorByType(String type) {
        for (AtomicRuleEvaluator evaluator : ruleEvaluators) {
            if (evaluator.getType().equals(type)) {
                return evaluator;
            }
        }
        return null;
    }
    
    /**
     * Represents a group of atomic rules that are evaluated with AND logic.
     */
    public static class RuleGroup {
        private List<AtomicRule> atomicRules;
        
        public RuleGroup(List<AtomicRule> atomicRules) {
            this.atomicRules = atomicRules;
        }
        
        public List<AtomicRule> getAtomicRules() {
            return atomicRules;
        }
        
        public void setAtomicRules(List<AtomicRule> atomicRules) {
            this.atomicRules = atomicRules;
        }
    }
    
    /**
     * Represents a single atomic rule with its type and parameters.
     */
    public static class AtomicRule {
        private String type;
        private Map<String, Object> params;
        
        public AtomicRule(String type, Map<String, Object> params) {
            this.type = type;
            this.params = params;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Map<String, Object> getParams() {
            return params;
        }
        
        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }
}