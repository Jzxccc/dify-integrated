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
- 前后端分离架构

## 新增功能：多轮会话与身份验证
本项目已实现多轮会话能力及用户身份验证功能，具体包括：

### 用户身份验证
- 用户注册和登录
- JWT令牌认证
- 会话管理
- 用户权限控制

### 多轮会话功能
- 创建和管理多个独立会话
- 会话历史记录
- 会话上下文保持
- 会话状态管理
- 用户会话隔离

### API 端点
#### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

#### 会话管理
- `POST /api/conversations` - 创建新会话
- `GET /api/conversations` - 获取当前用户的所有会话
- `GET /api/conversations/{conversationId}` - 获取特定会话详情
- `PUT /api/conversations/{conversationId}/end` - 结束会话

#### 应用交互（需认证）
- `POST /api/authenticated/app/{appId}/chat` - 认证用户聊天
- `POST /api/authenticated/app/{appId}/chat-stream` - 认证用户聊天（流式响应）
- `GET /api/authenticated/app/{appId}/history` - 获取会话历史
- `GET /api/authenticated/app/{appId}/conversations` - 获取用户在特定应用的会话列表

## 技术栈
- Java 21
- Spring Boot 3.2.0
- Spring WebFlux (响应式编程)
- Spring Data JPA
- Spring Security (JWT认证)
- PostgreSQL 数据库
- Redis (可选，用于会话管理)
- Thymeleaf (前端模板)
- Reactor 模式
- Bootstrap (前端样式)

## 快速开始
1. 启动 PostgreSQL 数据库
2. 配置 application.properties 中的数据库连接信息
3. 运行 `mvn spring-boot:run` 启动应用
4. 访问 `http://localhost:8080` 进行注册和登录

## 配置说明
在 `application.properties` 中配置以下参数：
```properties
# Dify API 配置
dify.api.base-url=https://api.dify.ai/v1
dify.api.default-api-key=your_dify_api_key_here

# 数据库配置
spring.datasource.url=jdbc:postgresql://localhost:5432/dify_integration
spring.datasource.username=postgres
spring.datasource.password=your_password

# JWT 配置
jwt.secret=your_secure_jwt_secret
jwt.expiration=86400000  # 24小时
```

## 前端界面
- `/` - 首页
- `/login` - 用户登录
- `/register` - 用户注册
- `/chat` - 聊天界面，支持多会话管理
- `/config` - 配置界面

## 安全特性
- JWT令牌认证，确保用户身份验证
- 会话隔离，用户只能访问自己的会话
- 请求验证，防止未授权访问
- 密码加密存储

## 数据模型
- `User` - 用户信息
- `Conversation` - 会话信息
- `AppInteraction` - 应用交互记录
- `ApiKey` - API密钥存储

## 部署说明
1. 确保已安装 Java 21 和 Maven
2. 配置数据库连接参数
3. 设置安全的JWT密钥
4. 运行 `mvn clean package` 构建项目
5. 运行 `java -jar target/dify-integration-1.0.0.jar` 启动应用