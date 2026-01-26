package com.dify.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FactSlot represents a predefined business dimension type.
 * All FactSlots are defined at design time as part of the schema-first approach.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactSlot {
    
    /**
     * Unique identifier for the fact slot
     */
    private String factId;
    
    /**
     * Business type of the fact
     */
    private String type;
    
    /**
     * Description of what this fact represents
     */
    private String description;
    
    /**
     * Whether this fact is required for certain actions
     */
    private boolean required;
    
    /**
     * Whether this fact can have multiple values
     */
    private boolean multiValue;
}