package com.dify.ai.controller;

import com.dify.ai.domain.model.FactSlot;
import com.dify.ai.service.FactSlotRegistry;
import com.dify.ai.service.FactSlotValidator;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

/**
 * 用于管理FactSlot定义的REST控制器。
 * 提供注册、检索和管理FactSlot定义的端点。
 */
@RestController
@RequestMapping("/api/fact-slots")
public class FactSlotController {

    @Resource
    private FactSlotRegistry factSlotRegistry;

    @Resource
    private FactSlotValidator factSlotValidator;

    /**
     * Get all registered FactSlots
     *
     * @return collection of all registered FactSlots
     */
    @GetMapping
    public ResponseEntity<Collection<FactSlot>> getAllFactSlots() {
        Collection<FactSlot> factSlots = factSlotRegistry.getAllFactSlots();
        return ResponseEntity.ok(factSlots);
    }

    /**
     * Get a specific FactSlot by its ID
     *
     * @param factId the fact identifier
     * @return the FactSlot with the given ID, or 404 if not found
     */
    @GetMapping("/{factId}")
    public ResponseEntity<FactSlot> getFactSlot(@PathVariable String factId) {
        Optional<FactSlot> factSlot = factSlotRegistry.getFactSlot(factId);
        return factSlot.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Register a new FactSlot
     *
     * @param factSlot the FactSlot to register
     * @return the registered FactSlot with 201 status, or 400 if validation fails
     */
    @PostMapping
    public ResponseEntity<?> registerFactSlot(@RequestBody FactSlot factSlot) {
        // Validate the FactSlot
        var errors = factSlotValidator.validate(factSlot);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Check if a FactSlot with this ID already exists
        if (factSlotRegistry.hasFactSlot(factSlot.getFactId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A FactSlot with ID '" + factSlot.getFactId() + "' already exists");
        }

        // Register the FactSlot
        factSlotRegistry.registerFactSlot(factSlot);
        return ResponseEntity.status(HttpStatus.CREATED).body(factSlot);
    }

    /**
     * Update an existing FactSlot
     *
     * @param factId the fact identifier
     * @param updatedFactSlot the updated FactSlot
     * @return the updated FactSlot with 200 status, or 404 if not found
     */
    @PutMapping("/{factId}")
    public ResponseEntity<?> updateFactSlot(@PathVariable String factId, @RequestBody FactSlot updatedFactSlot) {
        // Verify the factId in path matches the one in the body
        if (!factId.equals(updatedFactSlot.getFactId())) {
            return ResponseEntity.badRequest().body("FactSlot ID in path does not match ID in request body");
        }

        // Validate the FactSlot
        var errors = factSlotValidator.validate(updatedFactSlot);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // Check if the FactSlot exists
        if (!factSlotRegistry.hasFactSlot(factId)) {
            return ResponseEntity.notFound().build();
        }

        // Update the FactSlot by removing and re-adding
        factSlotRegistry.removeFactSlot(factId);
        factSlotRegistry.registerFactSlot(updatedFactSlot);
        return ResponseEntity.ok(updatedFactSlot);
    }

    /**
     * Delete a FactSlot by its ID
     *
     * @param factId the fact identifier to delete
     * @return 204 status on success, or 404 if not found
     */
    @DeleteMapping("/{factId}")
    public ResponseEntity<Void> deleteFactSlot(@PathVariable String factId) {
        boolean removed = factSlotRegistry.removeFactSlot(factId);
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}