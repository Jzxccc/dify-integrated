package com.dify.ai.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FactValue represents a specific value for a FactSlot in a particular request.
 * FactValue has a state that indicates whether it has been reviewed/confirmed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactValue {

    /**
     * The actual value of the fact
     */
    private Object value;

    /**
     * The state of the fact value (RAW or REVIEWED)
     */
    private State state;

    /**
     * Source of the fact value (e.g., USER_INPUT)
     */
    private String source;

    /**
     * Evidence of how this fact was derived (e.g., which rule triggered)
     */
    private String evidence;

    /**
     * Enumeration for the state of a FactValue
     */
    public enum State {
        /**
         * RAW: FactValue was initially extracted from user input or intermediate step,
         * but has not yet been confirmed/validated
         */
        RAW,

        /**
         * REVIEWED: FactValue has been validated by the Review Agent and confirmed
         */
        REVIEWED
    }
}