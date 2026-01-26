package com.dify.ai.service;

import com.dify.ai.domain.model.FactSlot;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FactSlotRegistry manages the predefined FactSlot definitions.
 * All FactSlots are defined at design time as part of the schema-first approach.
 */
@Service
public class FactSlotRegistry {

    private final Map<String, FactSlot> factSlots = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeDefaultFactSlots() {
        // Initialize with some default fact slots as examples
        // In a real implementation, these would be loaded from configuration or database
        
        // Example fact slots - these would typically be loaded from external configuration
        FactSlot supplierName = FactSlot.builder()
                .factId("SUPPLIER_NAME")
                .type("STRING")
                .description("Supplier name")
                .required(true)
                .multiValue(false)
                .build();
        
        FactSlot orderId = FactSlot.builder()
                .factId("ORDER_ID")
                .type("STRING")
                .description("Order identifier")
                .required(true)
                .multiValue(false)
                .build();
        
        FactSlot dateRange = FactSlot.builder()
                .factId("DATE_RANGE")
                .type("DATE_RANGE")
                .description("Date range for queries")
                .required(false)
                .multiValue(false)
                .build();
        
        factSlots.put(supplierName.getFactId(), supplierName);
        factSlots.put(orderId.getFactId(), orderId);
        factSlots.put(dateRange.getFactId(), dateRange);
    }

    /**
     * Register a new FactSlot definition
     *
     * @param factSlot the FactSlot to register
     * @throws IllegalArgumentException if the factSlot is invalid
     */
    public void registerFactSlot(FactSlot factSlot) {
        if (factSlot == null || factSlot.getFactId() == null || factSlot.getFactId().trim().isEmpty()) {
            throw new IllegalArgumentException("FactSlot and its factId must not be null or empty");
        }
        
        factSlots.put(factSlot.getFactId(), factSlot);
    }

    /**
     * Get a FactSlot by its ID
     *
     * @param factId the fact identifier
     * @return the FactSlot with the given ID, or null if not found
     */
    public Optional<FactSlot> getFactSlot(String factId) {
        return Optional.ofNullable(factSlots.get(factId));
    }

    /**
     * Get all registered FactSlots
     *
     * @return an unmodifiable collection of all registered FactSlots
     */
    public Collection<FactSlot> getAllFactSlots() {
        return Collections.unmodifiableCollection(factSlots.values());
    }

    /**
     * Check if a FactSlot with the given ID exists
     *
     * @param factId the fact identifier
     * @return true if a FactSlot with the given ID exists, false otherwise
     */
    public boolean hasFactSlot(String factId) {
        return factSlots.containsKey(factId);
    }

    /**
     * Remove a FactSlot by its ID
     *
     * @param factId the fact identifier to remove
     * @return true if the FactSlot was removed, false if it didn't exist
     */
    public boolean removeFactSlot(String factId) {
        return factSlots.remove(factId) != null;
    }
}