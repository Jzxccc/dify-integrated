package com.dify.ai.service;

import com.dify.ai.domain.model.FactValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AuditTrailService provides audit trail functionality for fact validation decisions.
 * It maintains a record of all fact validation activities as required by the design.
 */
@Service
public class AuditTrailService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditTrailService.class);
    
    private final List<AuditEntry> auditEntries = new ArrayList<>();
    
    /**
     * Records a fact validation decision in the audit trail.
     *
     * @param factValue the FactValue that was validated
     * @param validator the entity that performed the validation
     * @param decision the validation decision (e.g., "ACCEPTED", "REJECTED", "CONFIRMED")
     * @param reason the reason for the decision
     */
    public void recordFactValidation(FactValue factValue, String validator, String decision, String reason) {
        AuditEntry entry = new AuditEntry(
            LocalDateTime.now(),
            "FACT_VALIDATION",
            validator,
            factValue != null ? factValue.toString() : "null",
            decision,
            reason
        );
        
        auditEntries.add(entry);
        
        // Also log the event
        logger.info("Fact validation - Validator: {}, Decision: {}, Reason: {}, FactValue: {}", 
                   validator, decision, reason, factValue);
    }
    
    /**
     * Records a fact state transition in the audit trail.
     *
     * @param factValueBefore the FactValue before the transition
     * @param factValueAfter the FactValue after the transition
     * @param performer the entity that performed the transition
     * @param reason the reason for the transition
     */
    public void recordStateTransition(FactValue factValueBefore, FactValue factValueAfter, String performer, String reason) {
        String decision = String.format("State transition from %s to %s", 
                                      factValueBefore != null ? factValueBefore.getState() : "null",
                                      factValueAfter != null ? factValueAfter.getState() : "null");
        
        AuditEntry entry = new AuditEntry(
            LocalDateTime.now(),
            "STATE_TRANSITION",
            performer,
            String.format("Before: %s, After: %s", 
                         factValueBefore != null ? factValueBefore.toString() : "null",
                         factValueAfter != null ? factValueAfter.toString() : "null"),
            decision,
            reason
        );
        
        auditEntries.add(entry);
        
        // Also log the event
        logger.info("State transition - Performer: {}, Transition: {}, Reason: {}", 
                   performer, decision, reason);
    }
    
    /**
     * Records an ambiguity resolution decision in the audit trail.
     *
     * @param candidateValues the list of candidate values that were evaluated
     * @param selectedValue the value that was selected
     * @param resolver the entity that performed the resolution
     * @param reason the reason for the selection
     */
    public void recordAmbiguityResolution(List<FactValue> candidateValues, FactValue selectedValue, String resolver, String reason) {
        AuditEntry entry = new AuditEntry(
            LocalDateTime.now(),
            "AMBIGUITY_RESOLUTION",
            resolver,
            String.format("Candidates: %s, Selected: %s", candidateValues, selectedValue),
            "Selected value from candidates",
            reason
        );
        
        auditEntries.add(entry);
        
        // Also log the event
        logger.info("Ambiguity resolution - Resolver: {}, Candidates count: {}, Selected: {}, Reason: {}", 
                   resolver, candidateValues != null ? candidateValues.size() : 0, selectedValue, reason);
    }
    
    /**
     * Retrieves audit entries for a specific fact ID.
     *
     * @param factId the fact identifier to search for
     * @return a list of audit entries related to the fact
     */
    public List<AuditEntry> getAuditTrailForFact(String factId) {
        return auditEntries.stream()
                .filter(entry -> entry.getEntityInfo().contains(factId))
                .toList();
    }
    
    /**
     * Retrieves all audit entries.
     *
     * @return a list of all audit entries
     */
    public List<AuditEntry> getAllAuditEntries() {
        return new ArrayList<>(auditEntries);
    }
    
    /**
     * Clears the audit trail (for testing purposes).
     */
    public void clearAuditTrail() {
        auditEntries.clear();
    }
    
    /**
     * Represents an audit entry in the trail.
     */
    public static class AuditEntry {
        private final LocalDateTime timestamp;
        private final String eventType;
        private final String actor;
        private final String entityInfo;
        private final String action;
        private final String reason;
        
        public AuditEntry(LocalDateTime timestamp, String eventType, String actor, String entityInfo, 
                         String action, String reason) {
            this.timestamp = timestamp;
            this.eventType = eventType;
            this.actor = actor;
            this.entityInfo = entityInfo;
            this.action = action;
            this.reason = reason;
        }
        
        // Getters
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getEventType() { return eventType; }
        public String getActor() { return actor; }
        public String getEntityInfo() { return entityInfo; }
        public String getAction() { return action; }
        public String getReason() { return reason; }
        
        @Override
        public String toString() {
            return String.format("[%s] %s: %s performed '%s' on %s because %s", 
                               timestamp, eventType, actor, action, entityInfo, reason);
        }
    }
}