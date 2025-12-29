# Dify 应用交互规范

## 概述
本规范定义了与特定 Dify 应用（ID: d2a5c47c-5644-49f0-bc20-6a67ac1a7b69）交互的功能需求，包括 API 通信、数据持久化等。

## ADDED Requirements

### Requirement: 特定应用 API 通信
系统 SHALL be able to communicate with a specific Dify application through WebClient.

#### Scenario: 成功的特定应用 API 通信
给定一个有效的 API 密钥，
当用户发送消息到特定应用时，
系统应使用 WebClient 向 https://api.dify.ai/v1/apps/d2a5c47c-5644-49f0-bc20-6a67ac1a7b69/chat-messages 发送请求，
然后返回 Dify 应用的响应。

#### Scenario: 特定应用 API 通信失败
给定一个无效的 API 密钥或网络问题，
当用户发送消息到特定应用时，
系统应捕获异常并向用户显示错误信息。

### Requirement: 交互数据持久化
系统 SHALL persist interaction data with the specific Dify application to the PostgreSQL database.

#### Scenario: 成功持久化交互数据
当用户与特定 Dify 应用交互时，
系统应将交互的输入和输出数据存储到 AppInteraction 实体中，
包括用户 ID、应用 ID、输入内容、输出内容和时间戳。

#### Scenario: 持久化失败
当数据库连接不可用或发生其他持久化错误时，
系统应记录错误日志，
但仍应返回 Dify 应用的响应给用户。

### Requirement: 交互数据查询
系统 SHALL provide functionality to query persisted interaction data.

#### Scenario: 查询特定应用的交互记录
当管理员或用户查询与特定应用的交互历史时，
系统应从数据库中检索相关记录，
并按时间戳排序返回结果。

#### Scenario: 查询特定用户的交互记录
当需要查询特定用户的交互历史时，
系统应从数据库中检索相关记录，
并返回该用户的交互历史。