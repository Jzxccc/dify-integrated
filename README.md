# Dify 智能体集成平台

## 项目由来
此项目所有内容均使用`qwen-code` 搭建完成
使用openspec 做交互媒介

## 功能特性
- 与 Dify 平台智能体系统集成
- 用户身份验证和授权
- 多轮会话管理
- 会话历史记录
- 实时聊天功能

## 新增功能：多轮会话与身份验证
本项目已实现多轮会话能力及用户身份验证功能，具体包括：

### 用户身份验证
- 用户注册和登录
- JWT令牌认证
- 会话管理

### 多轮会话功能
- 创建和管理多个独立会话
- 会话历史记录
- 会话上下文保持

### API 端点
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/conversations` - 创建新会话
- `GET /api/conversations` - 获取用户会话列表
- `GET /api/conversations/{conversationId}` - 获取特定会话
- `PUT /api/conversations/{conversationId}/end` - 结束会话
- `POST /api/authenticated/app/{appId}/chat` - 认证用户聊天
- `GET /api/authenticated/app/{appId}/history` - 获取会话历史

## 技术栈
- Java 21
- Spring Boot
- Spring WebFlux
- PostgreSQL 数据库
- JWT 认证
- Reactor 模式

## 快速开始
1. 启动 PostgreSQL 数据库
2. 配置 application.properties 中的数据库连接信息
3. 运行 `mvn spring-boot:run` 启动应用
4. 访问 `http://localhost:8080` 进行注册和登录