# multi-conversation Specification

## Purpose
TBD - created by archiving change enable-multi-conversation. Update Purpose after archive.
## Requirements
### Requirement: 与Dify应用的会话上下文API通信
系统SHALL include conversation context when communicating with the Dify application, for authenticated users.

#### Scenario: 带会话上下文的API通信
- **WHEN** 认证用户在活跃会话中向Dify应用发送消息时（给定一个有效的JWT令牌和会话ID）
- **THEN** 系统应使用WebClient向Dify API发送请求，包含conversation_id参数，然后返回Dify应用的响应

#### Scenario: 无会话上下文的API通信
- **WHEN** 认证用户开始新对话时（给定一个有效的JWT令牌，但没有现有会话）
- **THEN** 系统应发起新的会话，不包含conversation_id参数，然后返回Dify应用的响应

