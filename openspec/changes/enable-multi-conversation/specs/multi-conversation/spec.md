# 多轮会话与身份验证功能规范

## Purpose
本规范定义了多轮会话功能和用户身份验证的需求，使系统能够支持用户与Dify智能体进行连续的对话，保持会话上下文，并通过身份验证确保每个用户拥有独立的会话，提供更安全和连贯的交互体验。

## ADDED Requirements

### Requirement: 用户身份验证
系统SHALL provide functionality to authenticate users before allowing access to conversation features.

#### Scenario: 用户成功登录
给定一个有效的用户名和密码，
当用户提交登录请求时，
系统应验证凭据，
生成JWT令牌，
并返回令牌给用户。

#### Scenario: 用户登录失败
给定无效的用户名或密码，
当用户提交登录请求时，
系统应返回错误信息，
且不生成JWT令牌。

### Requirement: 令牌管理
系统SHALL manage JWT tokens for user authentication and authorization.

#### Scenario: 令牌验证成功
给定一个有效的JWT令牌，
当用户请求需要认证的资源时，
系统应验证令牌的有效性，
允许用户访问请求的资源。

#### Scenario: 令牌验证失败
给定一个无效或过期的JWT令牌，
当用户请求需要认证的资源时，
系统应返回401未授权错误，
拒绝访问请求的资源。

### Requirement: 会话创建
系统SHALL provide functionality to create a new conversation session for authenticated users.

#### Scenario: 认证用户成功创建新会话
给定一个有效的JWT令牌，
当用户请求开始新会话时，
系统应验证令牌，
生成唯一的会话ID，
在数据库中创建与用户关联的Conversation记录，
并返回会话ID和初始会话信息。

#### Scenario: 未认证用户尝试创建会话
给定一个无效或缺失的JWT令牌，
当用户请求开始新会话时，
系统应返回401未授权错误，
且不创建新的会话记录。

### Requirement: 会话所有权验证
系统SHALL ensure users can only access their own conversations.

#### Scenario: 用户访问自己的会话
给定一个有效的JWT令牌和属于该用户的会话ID，
当用户请求访问会话时，
系统应验证用户身份和会话所有权，
返回会话信息。

#### Scenario: 用户尝试访问其他用户的会话
给定一个有效的JWT令牌和不属于该用户的会话ID，
当用户请求访问会话时，
系统应返回403禁止访问错误，
拒绝访问请求。

### Requirement: 会话消息交互
系统SHALL maintain conversation context across multiple message exchanges for authenticated users.

#### Scenario: 认证用户在会话中发送消息
给定一个有效的JWT令牌和活跃的会话，
当用户发送消息时，
系统应验证令牌，
将消息连同会话ID发送到Dify API，
接收响应并保存到数据库，
返回响应给用户。

#### Scenario: 未认证用户尝试发送消息
给定一个无效或缺失的JWT令牌，
当用户尝试发送消息时，
系统应返回401未授权错误，
不处理消息请求。

### Requirement: 会话历史管理
系统SHALL store and retrieve conversation history for each session, respecting user ownership.

#### Scenario: 认证用户获取自己的会话历史
给定一个有效的JWT令牌和会话ID，
当用户请求会话历史时，
系统应验证用户身份和会话所有权，
从数据库检索相关的交互记录，
按时间顺序返回消息历史。

#### Scenario: 会话历史分页
给定一个有大量消息的会话和有效的JWT令牌，
当用户请求会话历史时，
系统应支持分页查询，
返回指定范围的消息记录。

### Requirement: 会话结束
系统SHALL provide functionality to properly end a conversation session for authenticated users.

#### Scenario: 认证用户正常结束会话
给定一个有效的JWT令牌和属于该用户的活跃会话，
当用户请求结束会话时，
系统应验证用户身份和会话所有权，
标记会话为结束状态，
清理相关资源，
返回会话结束确认。

#### Scenario: 用户尝试结束其他用户的会话
给定一个有效的JWT令牌和不属于该用户的会话，
当用户请求结束会话时，
系统应返回403禁止访问错误，
不结束会话。

### Requirement: 会话查询
系统SHALL provide functionality to query conversations by user, respecting ownership.

#### Scenario: 认证用户查询自己的会话列表
给定一个有效的JWT令牌，
当用户请求查看其会话列表时，
系统应验证用户身份，
返回该用户的所有会话记录，
按创建时间排序。

#### Scenario: 认证用户查询特定会话
给定一个有效的JWT令牌和属于该用户的会话ID，
当用户请求特定会话详情时，
系统应验证用户身份和会话所有权，
返回会话的详细信息。

## MODIFIED Requirements

### Requirement: 与Dify应用的API通信
系统SHALL include conversation context when communicating with the Dify application, for authenticated users.

#### Scenario: 带会话上下文的API通信
给定一个有效的JWT令牌和活跃的会话，
当认证用户发送消息到Dify应用时，
系统应使用WebClient向Dify API发送请求，
包含conversation_id参数，
然后返回Dify应用的响应。

#### Scenario: 无会话上下文的API通信
给定一个有效的JWT令牌和用户开始新对话（无现有会话），
当用户发送消息到Dify应用时，
系统应发起新的会话，
不包含conversation_id参数，
然后返回Dify应用的响应。

## REMOVED Requirements
无