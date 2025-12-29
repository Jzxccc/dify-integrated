# WebClient 通信规范

## 概述
本规范定义了使用 Spring WebFlux 的 WebClient 与 Dify API 进行通信的功能需求。

## ADDED Requirements

### Requirement: WebClient 配置
系统应配置 WebClient 以与 Dify API 进行通信。

#### Scenario: WebClient 初始化
当应用程序启动时，
系统应创建一个 WebClient 实例，
该实例配置为使用 Dify API 的基础 URL（https://api.dify.ai/v1）。

### Requirement: 发送消息请求
系统应能够通过 WebClient 向 Dify API 发送消息请求。

#### Scenario: 成功发送消息
给定一个有效的 API 密钥和用户消息，
当系统向 Dify API 发送消息请求时，
WebClient 应成功发送请求并接收响应。

#### Scenario: 消息发送失败
给定无效的 API 密钥或网络问题，
当系统尝试发送消息请求时，
WebClient 应捕获异常并返回错误信息。

### Requirement: 处理 API 响应
系统应能够处理 Dify API 返回的响应。

#### Scenario: 接收有效响应
当 Dify API 返回有效响应时，
WebClient 应正确解析响应数据，
并将其传递给调用方。

#### Scenario: 接收错误响应
当 Dify API 返回错误响应时，
WebClient 应捕获错误并返回适当的错误信息。