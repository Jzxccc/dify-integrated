package com.dify.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Action represents a system-executable capability.
 * Actions are defined at design time and declare their requirements and outputs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    /**
     * Unique identifier for the action
     */
    private String actionId;

    /**
     * Name of the action
     */
    private String name;

    /**
     * Description of what the action does
     */
    private String description;

    /**
     * Set of factIds that this action requires to be in REVIEWED state before execution
     */
    @Builder.Default
    private Set<String> requires = Set.of();

    /**
     * Set of factIds that this action produces when executed
     */
    @Builder.Default
    private Set<String> produces = Set.of();
}