# 多轮会话与身份验证功能设计文档

## 架构概览
本设计文档描述了多轮会话功能及用户身份验证的架构设计，包括身份验证、会话管理、数据模型、API接口和与Dify平台的集成。

## 组件设计

### 1. 身份验证管理器 (AuthenticationManager)
- **功能**: 负责用户身份验证和JWT令牌管理
- **实现**:
  - 用户登录验证
  - JWT令牌生成和验证
  - 令牌刷新机制
  - 用户权限验证

### 2. 会话管理器 (ConversationManager)
- **功能**: 负责管理会话的整个生命周期
- **实现**:
  - 创建新会话并生成唯一会话ID
  - 维护活跃会话的状态
  - 处理会话超时和清理
  - 与Dify平台同步会话状态
  - 确保用户只能访问自己的会话

### 3. 会话服务 (ConversationService)
- **功能**: 提供会话相关的业务逻辑
- **实现**:
  - 会话CRUD操作
  - 会话历史管理
  - 会话状态持久化
  - 会话权限验证

### 4. 用户服务 (UserService)
- **功能**: 管理用户信息和身份验证
- **实现**:
  - 用户注册和登录
  - 用户信息管理
  - 密码加密和验证
  - 用户权限管理

### 5. 数据模型
- **User实体**:
  - 用户ID (user_id)
  - 用户名 (username)
  - 加密密码 (password)
  - 邮箱 (email)
  - 创建时间 (created_at)
  - 更新时间 (updated_at)
  - 激活状态 (active)

- **Conversation实体**:
  - 会话ID (conversation_id)
  - 用户ID (user_id) - 外键关联User
  - 应用ID (app_id)
  - 会话状态 (status: active, ended, archived)
  - 创建时间 (created_at)
  - 更新时间 (updated_at)
  - 结束时间 (ended_at)
  - 元数据 (metadata: JSONB)

- **AppInteraction实体** (扩展):
  - 添加conversation_id外键关联
  - 保持与现有交互数据的兼容性

### 6. API端点设计
- **POST /api/auth/login**: 用户登录
- **POST /api/auth/register**: 用户注册
- **POST /api/auth/logout**: 用户登出
- **POST /api/auth/refresh**: 刷新令牌
- **POST /api/conversations**: 创建新会话（需要认证）
- **GET /api/conversations/{conversationId}**: 获取特定会话详情（需要认证）
- **PUT /api/conversations/{conversationId}/end**: 结束会话（需要认证）
- **GET /api/conversations**: 获取当前用户的所有会话（需要认证）
- **GET /api/conversations/{conversationId}/messages**: 获取会话消息历史（需要认证）
- **扩展现有端点**以支持会话ID参数和身份验证

### 7. 与Dify API的集成
- **会话ID传递**: 在与Dify API通信时正确传递conversation_id
- **状态同步**: 确保本地会话状态与Dify平台保持一致
- **错误处理**: 处理Dify API返回的会话相关错误

## 数据流
1. 用户通过身份验证API进行登录
2. 系统验证用户凭据并生成JWT令牌
3. 用户发起新会话请求，携带JWT令牌
4. 系统验证令牌并创建新的Conversation记录
5. 用户发送消息时，系统验证令牌并将conversation_id传递给Dify API
6. 系统接收Dify响应并保存到AppInteraction表
7. 当会话结束时，更新Conversation记录的状态

## 安全考虑
- JWT令牌的安全生成和验证
- 密码使用强哈希算法（如BCrypt）加密
- 会话访问控制，确保用户只能访问自己的会话
- 令牌过期和刷新机制
- 会话超时机制防止资源滥用
- 会话数据的隐私保护

## 性能考虑
- JWT令牌的高效验证
- 会话数据的索引优化
- 会话历史的分页查询
- 会话缓存机制（如Redis）以提高性能
- 会话清理任务以管理存储空间