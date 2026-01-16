# 多会话功能性能测试指南

## 测试目标

验证多会话功能在高并发和大数据量场景下的性能表现。

## 测试环境配置

```properties
# application-test-performance.properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/dify_integration_test
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
spring.jpa.hibernate.ddl-auto=validate
logging.level.com.example.difyintegration=INFO
```

## 性能测试场景

### 1. 并发用户测试
- 目标：验证系统支持100个并发用户同时进行会话
- 方法：
  1. 创建100个测试用户
  2. 每个用户创建一个会话
  3. 每个用户发送10条消息
  4. 监控响应时间和系统资源使用情况

### 2. 大数据量会话测试
- 目标：验证系统在大量会话数据下的表现
- 方法：
  1. 创建1000个会话
  2. 每个会话包含100条交互记录
  3. 测试会话查询和历史加载性能

### 3. 长时间运行测试
- 目标：验证系统在长时间运行下的稳定性
- 方法：
  1. 运行系统72小时
  2. 模拟持续的用户活动
  3. 监控内存使用和会话清理功能

## 性能指标

- API响应时间：平均响应时间应小于500ms
- 并发用户支持：支持至少100个并发用户
- 数据库连接：连接池使用率应保持在80%以下
- 内存使用：GC频率应保持在正常范围
- 会话清理：过期会话应被正确清理

## 基准测试代码示例

```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-performance.properties")
class PerformanceTest {

    @Test
    @Disabled // 仅在性能测试时启用
    void testConcurrentUsers() throws InterruptedException {
        int numUsers = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numUsers);
        CountDownLatch latch = new CountDownLatch(numUsers);
        AtomicLong totalTime = new AtomicLong(0);

        for (int i = 0; i < numUsers; i++) {
            final int userId = i;
            executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    // 模拟用户操作
                    String token = loginAsUser("user" + userId);
                    String conversationId = createConversation(token);
                    for (int j = 0; j < 10; j++) {
                        sendMessage(token, conversationId, "Message " + j);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    totalTime.addAndGet(endTime - startTime);
                    latch.countDown();
                }
            });
        }

        latch.await();
        long avgTime = totalTime.get() / numUsers;
        System.out.println("Average time per user: " + avgTime + "ms");
        
        // 验证平均响应时间
        assertTrue(avgTime < 500, "Average response time should be less than 500ms");
    }
}
```

## 优化建议

1. 使用Redis缓存频繁访问的会话数据
2. 实现数据库查询优化，添加适当的索引
3. 使用连接池管理数据库连接
4. 实现消息队列处理大量并发请求
5. 定期清理过期会话以减少数据库负担

## 部署检查清单

- [ ] 数据库连接池配置
- [ ] JWT密钥安全配置
- [ ] Dify API密钥安全配置
- [ ] 日志级别配置
- [ ] 会话超时设置
- [ ] 安全头配置
- [ ] 监控和告警设置
- [ ] 备份策略配置