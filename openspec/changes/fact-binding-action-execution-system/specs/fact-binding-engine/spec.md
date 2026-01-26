## ADDED Requirements

### Requirement: Map user input to predefined fact slots
The system SHALL map user input to predefined FactSlot types using trigger rules and atomic conditions, generating RAW FactValue instances.

#### Scenario: Successful fact binding
- **WHEN** user input contains information that matches trigger rules for a FactSlot
- **THEN** the system creates a RAW FactValue bound to that FactSlot with appropriate value and evidence

### Requirement: Support multiple trigger rule types
The system SHALL support multiple types of atomic rules including CONTAINS, REGEX, DICT_MATCH, and ENTITY_ALIAS for fact binding.

#### Scenario: CONTAINS rule match
- **WHEN** user input contains a keyword specified in a CONTAINS rule
- **THEN** the system binds the input to the corresponding FactSlot

#### Scenario: REGEX rule match
- **WHEN** user input matches a regular expression specified in a REGEX rule
- **THEN** the system extracts the value and binds it to the corresponding FactSlot

#### Scenario: DICT_MATCH rule match
- **WHEN** user input matches an entry in a dictionary specified in a DICT_MATCH rule
- **THEN** the system binds the input to the corresponding FactSlot

#### Scenario: ENTITY_ALIAS rule match
- **WHEN** user input matches an entity alias specified in an ENTITY_ALIAS rule
- **THEN** the system binds the input to the corresponding FactSlot

### Requirement: Store binding evidence
The system SHALL store evidence of how each FactValue was derived from user input, including the specific rule that triggered the binding.

#### Scenario: Evidence recording
- **WHEN** a FactValue is created through fact binding
- **THEN** the system records the source (USER_INPUT) and the specific rule that caused the binding

### Requirement: Support OR/AND rule combinations
The system SHALL support combining multiple trigger rules with OR logic and multiple atomic rules within a group with AND logic.

#### Scenario: Rule group evaluation
- **WHEN** evaluating a FactSlot with multiple RuleGroups
- **THEN** the system applies OR logic between groups and AND logic within each group to determine if binding should occur