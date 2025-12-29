# API 端点规范

## 概述
本规范定义了与特定 Dify 应用交互的 API 端点要求。

## ADDED Requirements

### Requirement: 特定应用消息发送
系统 SHALL provide API 端点用于向特定 Dify 应用发送消息。

#### Scenario: 成功发送消息到特定应用
当用户通过 API 端点发送消息到特定应用时（POST /api/app/d2a5c47c-5644-49f0-bc20-6a67ac1a7b69/chat），
系统应将消息转发到对应的 Dify 应用，
存储交互数据到数据库，
并返回应用的响应。

### Requirement: 交互历史查询
系统 SHALL provide API 端点用于查询与特定应用的交互历史。

#### Scenario: 查询特定应用的交互历史
当用户查询与特定应用的交互历史时（GET /api/app/d2a5c47c-5644-49f0-bc20-6a67ac1a7b69/history），
系统应从数据库检索相关记录，
并返回交互历史列表。

#### Scenario: 查询特定用户的交互历史
当用户查询自己的交互历史时（GET /api/app/d2a5c47c-5644-49f0-bc20-6a67ac1a7b69/history?user={userId}），
系统应从数据库检索该用户的交互记录，
并返回交互历史列表。