# AI服务注解功能规范

## Purpose
本规范定义了AI服务注解系统的需求，使系统能够通过注解自动生成JSON Schema，从而让大模型和agent框架能够更好地理解和使用系统中的原子业务或工具。

## ADDED Requirements

### Requirement: AI服务注解定义
系统SHALL provide annotations to mark services and parameters that are accessible to AI models.

#### Scenario: 服务方法注解
- **WHEN** developer annotates a service method with AIService annotation
- **THEN** the system identifies the method as an AI-callable service
- **AND** extracts its metadata for Schema generation

#### Scenario: 参数注解
- **WHEN** developer annotates a parameter of an AI service method with AIParam annotation
- **THEN** the system recognizes the parameter attributes (name, type, description, required, etc.)
- **AND** includes them in the generated Schema

### Requirement: JSON Schema生成
系统SHALL generate JSON Schemas from annotated methods and parameters.

#### Scenario: 生成服务Schema
- **WHEN** the system requests the Schema for a method annotated with AIService
- **THEN** the system returns a description compliant with JSON Schema specification
- **AND** includes service name, description, parameter list and type information

#### Scenario: 生成参数Schema
- **WHEN** the system generates the service Schema for a parameter annotated with AIParam
- **THEN** the system includes the parameter's type, description, required status, etc. in the Schema
- **AND** ensures AI models can correctly understand parameter requirements

### Requirement: 服务注册与发现
系统SHALL register and allow discovery of AI-annotated services.

#### Scenario: 服务注册
- **WHEN** the system starts up with a method annotated with AIService
- **THEN** the system scans and registers the service
- **AND** makes it accessible through service discovery mechanism

#### Scenario: 服务查询
- **WHEN** a user requests the list of available services for registered AI services
- **THEN** the system returns all accessible services and their Schemas
- **AND** allows AI models to understand available functions

### Requirement: 服务执行
系统SHALL execute AI-initiated service calls based on annotation metadata.

#### Scenario: 服务调用执行
- **WHEN** the system receives an AI-generated service call request
- **THEN** the system validates parameters against Schema
- **AND** executes the corresponding service method
- **AND** returns the result to the AI model

#### Scenario: 参数验证
- **WHEN** the system executes an AI service call request
- **THEN** the system validates parameter types and values comply with Schema definition
- **AND** rejects calls that do not meet requirements

### Requirement: Agent接口适配
系统SHALL provide standardized interfaces for agent frameworks to discover and invoke services.

#### Scenario: 服务发现
- **WHEN** an agent framework requests the list of available services
- **THEN** the system returns metadata and Schema for all available services
- **AND** allows the agent to understand each service's functionality and usage

#### Scenario: 统一服务调用
- **WHEN** the system receives a service call initiated by an agent
- **THEN** the system executes the corresponding service method based on service name and parameters
- **AND** returns a standardized result format
- **AND** facilitates the agent's processing of subsequent logic

### Requirement: 服务分类与组织
系统SHALL organize services by functional domains to facilitate agent discovery and usage.

#### Scenario: 按功能域分类服务
- **WHEN** the system generates the service directory for multiple AI services
- **THEN** the system categorizes services by functional domain (such as user management, session management, message processing, etc.)
- **AND** enables agents to quickly locate required functions

#### Scenario: 服务标签和元数据
- **WHEN** the system registers an AI service
- **THEN** the system extracts service tags and metadata
- **AND** supports searching and filtering services by tags
- **AND** improves agent efficiency in using services