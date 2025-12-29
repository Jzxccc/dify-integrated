# Project Context

## Purpose
本项目与 Dify 平台集成，提供智能代理功能。它作为后端服务连接到 Dify 的代理系统，启用增强的 AI 驱动功能和工作流程。该系统采用现代响应式编程范式设计，具有可扩展性、响应性和可维护性。

## Tech Stack
- Java 21
- Spring Boot
- Spring WebFlux
- PostgreSQL 数据库
- Redis（用于缓存）
- Docker（用于容器化）
- Maven（用于构建管理）

## Project Conventions

### Code Style
- 遵循 Oracle Java 代码约定，参考 Google Java 风格指南
- 变量和方法名使用驼峰命名法
- 类名使用帕斯卡命名法
- 常量使用常量命名法（CONSTANT_CASE）
- 在适当的地方使用 Lombok 注解减少样板代码
- 为变量、方法和类使用有意义和描述性的名称
- 使用 4 个空格缩进格式化代码
- 最大行长度为 120 个字符

### Architecture Patterns
- 使用 Spring WebFlux 进行响应式编程，实现非阻塞、事件驱动架构
- RESTful API 设计原则
- 分层架构，清晰分离关注点（Controller、Service、Repository）
- 使用 DTO（数据传输对象）定义 API 合约
- 数据访问抽象的存储库模式
- 业务逻辑封装的服务层
- 配置类用于管理应用程序属性
- 在适当的地方使用 Spring 事件进行事件驱动通信

### Testing Strategy
- 使用 JUnit 5 和 Mockito 进行业务逻辑单元测试
- 使用 @SpringBootTest 对 API 端点和数据库交互进行集成测试
- 使用 WebTestClient 测试响应式控制器
- 使用 Testcontainers 进行真实数据库实例的集成测试
- 代码覆盖率目标为 80% 或更高
- 使用 Spring REST Docs 或 WireMock 进行 API 端点的契约测试

### Git Workflow
- 特性分支工作流程（Git Flow）
- 分支命名约定：feature/issue-id-description, bugfix/issue-id-description
- 压缩合并拉取请求以保持历史记录整洁
- 提交消息遵循约定式提交格式：type(scope): description
- 常见类型：feat、fix、docs、style、refactor、test、chore
- 拉取请求需要至少一次批准才能合并

## Domain Context
- 与 Dify 平台智能代理系统的集成
- 了解 AI/ML 概念和术语
- 了解基于代理的系统及其通信模式
- 熟悉自然语言处理概念
- 了解异步处理和事件驱动架构
- 了解用于性能优化的缓存策略

## Important Constraints
- 必须保持与 Dify 平台 API 规范的兼容性
- 性能要求：95% 的 API 响应时间在 500 毫秒以下
- 可扩展性：系统必须能够高效处理并发用户和请求
- 安全性：所有 API 端点必须实现适当的认证和授权
- 数据一致性：正确的数据库操作事务管理
- 遵守数据隐私法规（如 GDPR 等）

## External Dependencies
- Dify 平台 API 用于智能代理集成
- PostgreSQL 数据库用于持久数据存储
- Redis 用于缓存和会话管理
- Docker 注册表用于容器镜像管理
- OAuth 2.0 / OpenID Connect 提供商用于认证（如适用）
- SMTP 服务器用于电子邮件通知（如需要）
