## Context

The current agent system lacks a structured, deterministic approach to processing user input. The proposed fact-binding and action execution system introduces a schema-first architecture that separates concerns between fact extraction and action execution. This design addresses the need for a predictable, auditable, and maintainable agent system by implementing strong constraints between user input and business execution.

The system follows a clear pipeline: User Input → Intent Recognition → Fact Binding → FactsState (RAW) → Review Agent → FactsState (REVIEWED) → Action Planner → Action Execution. This architecture ensures that all business logic is triggered through well-defined fact states rather than ad-hoc interpretations of user input.

## Goals / Non-Goals

**Goals:**
- Implement a schema-first architecture where all fact types are defined at design time
- Create a deterministic fact binding system using rule-based triggers rather than LLM interpretation
- Establish clear separation between fact recognition and action execution
- Ensure all facts are validated by a Review Agent before action execution
- Maintain full auditability and traceability of the decision-making process
- Enable predictable and testable agent behavior

**Non-Goals:**
- Implement dynamic creation of fact types at runtime
- Allow LLMs to directly determine action parameters without fact validation
- Bypass the Review Agent for fact confirmation
- Support free-form semantic understanding without constraint

## Decisions

1. **FactSlot Definition Approach**: Using a schema-first approach where all FactSlots are defined at design time rather than dynamically generated. This ensures predictability and maintainability.

2. **State Management**: Implementing a two-state fact system (RAW/REVIEWED) to distinguish between initially extracted facts and confirmed facts. This adds a validation layer before action execution.

3. **Rule-Based Fact Binding**: Using trigger rules with atomic conditions (CONTAINS, REGEX, DICT_MATCH, ENTITY_ALIAS) instead of relying on LLMs for fact extraction. This ensures deterministic behavior and auditability.

4. **Action Execution Constraints**: Requiring all required facts to be in REVIEWED state before action execution. This prevents actions from being executed based on unconfirmed assumptions.

5. **Intent Recognition Boundaries**: Strictly limiting Intent Recognition to identifying user intent without generating facts or determining action parameters. This maintains clear separation of concerns.

6. **Technology Stack**: Leveraging the existing Java 21, Spring Boot, and Spring WebFlux stack to implement the new components, maintaining consistency with the existing architecture.

## Risks / Trade-offs

[Risk: Rigid schema may limit flexibility] → Mitigation: Regular review and update process for FactSlot definitions to accommodate evolving business needs

[Risk: Complex rule maintenance] → Mitigation: Providing clear tooling and interfaces for managing trigger rules, with version control and testing

[Risk: Performance overhead from validation layers] → Mitigation: Optimizing the Review Agent with caching and efficient validation algorithms

[Risk: Increased development complexity] → Mitigation: Clear documentation and modular design to isolate the complexity within well-defined components

[Risk: Reduced apparent "intelligence" due to constraints] → Mitigation: Focus on reliability and predictability as key benefits, with clear metrics showing improved accuracy and auditability