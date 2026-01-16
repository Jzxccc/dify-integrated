## 1. 实现注解定义
- [x] 创建AIService注解
- [x] 创建AIParam注解
- [x] 定义注解的属性和用途

## 2. 实现JSON Schema生成器
- [x] 创建SchemaGenerator服务
- [x] 实现从注解到JSON Schema的转换逻辑
- [x] 支持复杂数据类型和嵌套对象
- [ ] 添加单元测试验证生成的Schema

## 3. 实现运行时服务发现
- [x] 创建AIServiceRegistry服务
- [x] 实现扫描和注册带注解的方法
- [x] 提供服务列表和Schema查询接口
- [x] 添加服务调用代理机制

## 4. 更新现有服务
- [x] 创建AIServiceAdapter作为AI服务适配器
- [x] 为ConversationService添加注解
- [x] 为UserService添加注解
- [x] 为DifyApiClient添加注解
- [x] 为AppInteractionService添加注解

## 5. 创建AI服务调用接口
- [x] 创建AIServiceExecutor
- [x] 实现动态方法调用机制
- [x] 添加错误处理和验证
- [x] 实现参数验证和类型转换

## 6. Agent接口适配
- [x] 实现服务发现API
- [x] 实现统一服务调用入口
- [ ] 实现服务调用链路追踪
- [ ] 实现结构化结果返回

## 7. 服务分类与组织
- [x] 实现服务分类功能
- [x] 实现服务标签系统
- [x] 实现按功能域组织服务

## 8. 集成和测试
- [ ] 创建端到端测试
- [ ] 验证agent可以正确使用服务
- [ ] 测试各种参数类型和边界条件
- [ ] 性能测试和优化