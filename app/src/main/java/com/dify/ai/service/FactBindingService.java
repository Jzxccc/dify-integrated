package com.dify.ai.service;

import com.dify.ai.domain.model.FactSlot;
import com.dify.ai.domain.model.FactValue;
import com.dify.ai.domain.model.FactsState;
import com.dify.ai.rule.RuleGroupEvaluator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FactBindingService maps user input to FactValues using trigger rules.
 * It implements the fact binding engine as described in the design.
 */
@Service
public class FactBindingService {

    @Resource
    private FactSlotRegistry factSlotRegistry;

    @Resource
    private RuleGroupEvaluator ruleGroupEvaluator;

    /**
     * Binds user input to facts using trigger rules associated with FactSlots.
     *
     * @param userInput the raw user input to process
     * @return a FactsState containing RAW FactValues derived from the user input
     */
    public FactsState bindFacts(String userInput) {
        FactsState factsState = new FactsState();
        
        // Iterate through all registered FactSlots to see if any rules match
        for (FactSlot factSlot : factSlotRegistry.getAllFactSlots()) {
            // Get the trigger rules for this FactSlot (in a real implementation, these would be stored somewhere)
            List<RuleGroupEvaluator.RuleGroup> triggerRules = getTriggerRulesForFactSlot(factSlot.getFactId());
            
            if (!triggerRules.isEmpty()) {
                // Evaluate the trigger rules for this FactSlot
                boolean shouldBind = ruleGroupEvaluator.evaluateRuleGroups(userInput, triggerRules);
                
                if (shouldBind) {
                    // Extract the value from user input based on the rule that matched
                    // In a real implementation, this would be more sophisticated
                    Object extractedValue = extractValueFromInput(userInput, triggerRules);
                    
                    // Create a RAW FactValue
                    FactValue factValue = FactValue.builder()
                            .value(extractedValue)
                            .state(FactValue.State.RAW)
                            .source("USER_INPUT")
                            .evidence(buildEvidence(triggerRules))
                            .build();
                    
                    // Add the FactValue to the FactsState
                    factsState.addFactValue(factSlot.getFactId(), factValue);
                }
            }
        }
        
        return factsState;
    }

    /**
     * Gets the trigger rules associated with a specific FactSlot.
     * In a real implementation, these would be retrieved from a configuration or database.
     *
     * @param factId the fact identifier
     * @return a list of rule groups for the given fact
     */
    private List<RuleGroupEvaluator.RuleGroup> getTriggerRulesForFactSlot(String factId) {
        // In a real implementation, this would fetch rules from a configuration or database
        // For now, we'll return an empty list as an example
        // Different fact IDs would have different trigger rules
        
        List<RuleGroupEvaluator.RuleGroup> ruleGroups = new ArrayList<>();
        
        // Example: For SUPPLIER_NAME fact, we might have rules that look for supplier-related keywords
        if ("SUPPLIER_NAME".equals(factId)) {
            // Example rule group: Look for common supplier-related terms
            List<RuleGroupEvaluator.AtomicRule> atomicRules = List.of(
                new RuleGroupEvaluator.AtomicRule("CONTAINS", Map.of("keyword", "supplier")),
                new RuleGroupEvaluator.AtomicRule("CONTAINS", Map.of("keyword", "vendor"))
            );
            ruleGroups.add(new RuleGroupEvaluator.RuleGroup(atomicRules));
        } else if ("ORDER_ID".equals(factId)) {
            // Example rule group: Look for patterns that might indicate an order ID
            List<RuleGroupEvaluator.AtomicRule> atomicRules = List.of(
                new RuleGroupEvaluator.AtomicRule("REGEX", Map.of("pattern", "\\b[A-Z]{2}\\d{6}\\b")) // e.g., AB123456
            );
            ruleGroups.add(new RuleGroupEvaluator.RuleGroup(atomicRules));
        }
        
        return ruleGroups;
    }

    /**
     * Extracts a value from the user input based on the matching rules.
     * This is a simplified implementation - in reality, this would be more sophisticated.
     *
     * @param userInput the raw user input
     * @param triggerRules the rules that matched
     * @return the extracted value
     */
    private Object extractValueFromInput(String userInput, List<RuleGroupEvaluator.RuleGroup> triggerRules) {
        // This is a simplified implementation
        // In a real implementation, this would extract the specific value that matched the rule
        
        // For now, just return the entire input as the value
        // In practice, you'd want to extract only the relevant part
        return userInput;
    }

    /**
     * Builds evidence string to record how the fact was derived.
     *
     * @param triggerRules the rules that were used to derive the fact
     * @return evidence string
     */
    private String buildEvidence(List<RuleGroupEvaluator.RuleGroup> triggerRules) {
        StringBuilder evidence = new StringBuilder("Derived using ");
        evidence.append(triggerRules.size()).append(" rule group(s)");
        
        // In a real implementation, you'd include more specific details about which rules matched
        return evidence.toString();
    }
}