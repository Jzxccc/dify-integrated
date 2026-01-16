# Dify 智能体集成平台 - 项目沉淀文档

## 项目概述

Dify 智能体集成平台是一个基于 Spring Boot WebFlux 的响应式应用，旨在与 Dify 平台智能体系统集成，提供用户身份验证、多轮会话管理和实时聊天功能。

## 业务场景

### 主要功能
1. **用户身份验证** - 提供用户注册、登录和 JWT 令牌管理
2. **多轮会话管理** - 支持用户创建和管理多个独立的会话
3. **会话历史记录** - 保存和检索会话消息历史
4. **实时聊天功能** - 与 Dify 智能体进行实时交互
5. **会话上下文保持** - 在多轮对话中保持会话状态

### 使用场景
- 用户通过 Web 界面进行注册和登录
- 用户创建新的会话并与 Dify 智能体进行多轮对话
- 用户可以切换和查看历史会话
- 用户的会话数据被安全地隔离和保护

## 编码风格

### Java 编码规范
- 使用 Java 21 版本
- 遵循驼峰命名法
- 类名使用大写字母开头的驼峰命名（PascalCase）
- 方法名和变量名使用小写字母开头的驼峰命名（camelCase）
- 常量使用全大写字母，单词间用下划线分隔

### 项目结构
```
src/
├── main/
│   ├── java/
│   │   └── com/example/difyintegration/
│   │       ├── config/          # 配置类
│   │       ├── controller/      # 控制器层
│   │       ├── dto/            # 数据传输对象
│   │       ├── entity/         # 实体类
│   │       ├── exception/      # 异常处理
│   │       ├── repository/     # 数据访问层
│   │       ├── security/       # 安全相关
│   │       ├── service/        # 业务逻辑层
│   │       ├── util/           # 工具类
│   │       └── DifyIntegrationApplication.java
│   └── resources/
│       ├── sql/               # SQL 脚本
│       ├── static/            # 静态资源
│       ├── templates/         # 模板文件
│       └── application.properties
```

### 代码风格
- 使用 Lombok 注解减少样板代码（@Data, @RequiredArgsConstructor等）
- 使用 Spring 注解进行依赖注入（@Component, @Service, @Controller等）
- 使用响应式编程范式（Mono, Flux）
- 遵循单一职责原则，每个类只负责一个功能领域

## 组件功能

### 核心组件

#### 1. 用户管理模块
- **UserService**: 管理用户注册、登录和信息查询
- **ReactiveUserDetailsServiceImpl**: 提供响应式的用户详情服务
- **AuthenticationService**: 处理用户认证逻辑

#### 2. 会话管理模块
- **ConversationService**: 管理会话的整个生命周期
- **ConversationController**: 提供会话管理的 REST API
- **Conversation Entity**: 表示会话数据模型

#### 3. 交互管理模块
- **AppInteractionService**: 处理应用交互逻辑
- **AppInteraction Entity**: 存储交互记录

#### 4. Dify API 集成模块
- **DifyApiClient**: 与 Dify API 通信的基础客户端
- **DifyAppClient**: 专门处理应用级别的 Dify API 调用
- **AgentConversationManager**: 管理与 Dify 智能体的对话

#### 5. 安全模块
- **JwtUtil**: JWT 令牌的生成和验证
- **JwtAuthenticationWebFilter**: JWT 认证过滤器
- **SecurityConfig**: 安全配置
- **AuthenticationConfig**: 认证配置

#### 6. Web 层
- **AuthenticationController**: 处理用户认证相关请求
- **AuthenticatedAppController**: 处理需要认证的应用请求
- **DifyController**: 处理与 Dify API 相关的请求
- **PageController**: 处理页面跳转请求

### 数据模型

#### User 实体
- 用户基本信息（ID、用户名、邮箱、密码等）
- 支持用户认证和授权

#### Conversation 实体
- 会话标识符
- 关联的用户和应用
- 会话状态和元数据

#### AppInteraction 实体
- 交互记录（输入、输出）
- 关联的会话和用户
- 时间戳和元数据

### 配置组件

#### 应用配置
- **application.properties**: 主要配置文件
- **PasswordConfig**: 密码编码器配置
- **SecurityConfig**: 安全配置
- **AuthenticationConfig**: 认证配置

#### 数据库配置
- PostgreSQL 数据库连接
- JPA 配置
- 连接池配置（HikariCP）

## 技术栈

- **后端**: Spring Boot 3.2.0, Spring WebFlux, Spring Data JPA
- **安全**: Spring Security, JWT
- **数据库**: PostgreSQL
- **缓存**: Redis（可选）
- **前端**: Thymeleaf, Bootstrap
- **构建工具**: Maven
- **其他**: Lombok, JJWT

## OpenSpec 规范

本项目使用 OpenSpec 进行变更管理，主要变更包括：

### enable-multi-conversation 变更
- 添加了多轮会话功能
- 实现了用户身份验证和会话所有权验证
- 扩展了数据模型以支持会话管理
- 添加了相应的 API 端点和前端界面

## 部署和运维

### 部署要求
- Java 21 运行时环境
- PostgreSQL 数据库
- Redis（可选，用于会话管理）
- 配置 Dify API 密钥

### 配置要点
- 设置安全的 JWT 密钥
- 配置数据库连接参数
- 设置 Dify API 密钥
- 调整连接池参数

## 最佳实践

1. **安全性**:
   - 使用强密码策略
   - 定期更换 JWT 密钥
   - 实施适当的输入验证

2. **性能**:
   - 合理配置数据库连接池
   - 使用缓存减少数据库访问
   - 实现适当的分页查询

3. **可维护性**:
   - 遵循单一职责原则
   - 使用适当的日志记录
   - 编写单元测试和集成测试

## 扩展方向

1. **功能增强**:
   - 添加更多会话管理功能
   - 实现消息搜索功能
   - 添加会话分享功能

2. **性能优化**:
   - 实现消息压缩
   - 添加异步处理
   - 优化数据库查询

3. **安全增强**:
   - 实现多因素认证
   - 添加审计日志
   - 实现更细粒度的权限控制