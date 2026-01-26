package com.dify.ai.service;

import com.dify.ai.domain.model.FactSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * FactSlotValidator provides validation logic for FactSlot objects.
 * Ensures that all required properties are present and valid.
 */
@Component
public class FactSlotValidator {

    /**
     * Validates a FactSlot object to ensure all required properties are present.
     *
     * @param factSlot the FactSlot to validate
     * @return a list of validation errors, empty if the FactSlot is valid
     */
    public List<String> validate(FactSlot factSlot) {
        List<String> errors = new ArrayList<>();
        
        if (factSlot == null) {
            errors.add("FactSlot cannot be null");
            return errors;
        }
        
        if (factSlot.getFactId() == null || factSlot.getFactId().trim().isEmpty()) {
            errors.add("factId is required and cannot be null or empty");
        } else if (!isValidFactId(factSlot.getFactId())) {
            errors.add("factId must be alphanumeric and underscores only");
        }
        
        if (factSlot.getType() == null || factSlot.getType().trim().isEmpty()) {
            errors.add("type is required and cannot be null or empty");
        }
        
        if (factSlot.getDescription() == null || factSlot.getDescription().trim().isEmpty()) {
            errors.add("description is required and cannot be null or empty");
        }
        
        return errors;
    }
    
    /**
     * Checks if a FactSlot is valid.
     *
     * @param factSlot the FactSlot to validate
     * @return true if the FactSlot is valid, false otherwise
     */
    public boolean isValid(FactSlot factSlot) {
        return validate(factSlot).isEmpty();
    }
    
    /**
     * Validates that the factId follows proper naming conventions.
     * Fact IDs should be alphanumeric with underscores, and in uppercase convention.
     *
     * @param factId the fact ID to validate
     * @return true if the fact ID is valid, false otherwise
     */
    private boolean isValidFactId(String factId) {
        // Fact IDs should be alphanumeric with underscores, and typically uppercase
        return factId.matches("^[A-Z][A-Z0-9_]*[A-Z0-9]$");
    }
}