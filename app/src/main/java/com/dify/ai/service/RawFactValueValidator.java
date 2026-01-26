package com.dify.ai.service;

import com.dify.ai.domain.model.FactValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RawFactValueValidator provides validation logic for RAW FactValues.
 * It checks if RAW FactValues are reasonable before they are confirmed as REVIEWED.
 */
@Service
public class RawFactValueValidator {

    /**
     * Validates a RAW FactValue to check if it's reasonable.
     *
     * @param factValue the RAW FactValue to validate
     * @return a list of validation issues, empty if the FactValue is reasonable
     */
    public List<String> validateRawFactValue(FactValue factValue) {
        List<String> issues = new ArrayList<>();
        
        if (factValue == null) {
            issues.add("FactValue cannot be null");
            return issues;
        }
        
        if (factValue.getState() != FactValue.State.RAW) {
            issues.add("Validation is only for RAW FactValues");
            return issues;
        }
        
        if (factValue.getValue() == null) {
            issues.add("RAW FactValue should not have a null value");
        } else {
            // Perform type-specific validation based on the value
            validateValueType(factValue.getValue(), issues);
        }
        
        if (factValue.getSource() == null || factValue.getSource().trim().isEmpty()) {
            issues.add("RAW FactValue must have a source");
        }
        
        if (factValue.getEvidence() == null || factValue.getEvidence().trim().isEmpty()) {
            issues.add("RAW FactValue must have evidence of how it was derived");
        }
        
        return issues;
    }
    
    /**
     * Checks if a RAW FactValue is valid.
     *
     * @param factValue the RAW FactValue to validate
     * @return true if the FactValue is valid, false otherwise
     */
    public boolean isValidRawFactValue(FactValue factValue) {
        return validateRawFactValue(factValue).isEmpty();
    }
    
    /**
     * Performs type-specific validation on the value.
     *
     * @param value the value to validate
     * @param issues the list to add validation issues to
     */
    private void validateValueType(Object value, List<String> issues) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.trim().isEmpty()) {
                issues.add("String value should not be empty or just whitespace");
            }
        } else if (value instanceof Number) {
            // For numbers, we might want to check for reasonable bounds depending on the context
            // For now, just ensure it's not NaN or infinite if it's a float/double
            if (value instanceof Double) {
                Double dValue = (Double) value;
                if (dValue.isNaN() || dValue.isInfinite()) {
                    issues.add("Numeric value should not be NaN or infinite");
                }
            } else if (value instanceof Float) {
                Float fValue = (Float) value;
                if (fValue.isNaN() || fValue.isInfinite()) {
                    issues.add("Numeric value should not be NaN or infinite");
                }
            }
        }
        // Add more type validations as needed
    }
}