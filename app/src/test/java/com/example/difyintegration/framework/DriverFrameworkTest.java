package com.example.difyintegration.framework;

import com.example.difyintegration.service.AIServiceExecutor;
import com.example.difyintegration.service.AIServiceRegistry;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DriverFrameworkTest {

    @Resource
    private DriverFramework driverFramework;

    @Resource
    private AIServiceRegistry aiServiceRegistry;  // 修正：使用正确的类型

    @Resource
    private AIServiceExecutor serviceExecutor;

    @Test
    void testInitialize() {
        // 执行初始化方法
        driverFramework.initialize();

        // 验证框架已初始化
        assertNotNull(driverFramework.getServiceRegistry());
        assertNotNull(driverFramework.getServiceExecutor());
    }

    @Test
    void testExecuteServiceCall() {
        // 准备测试数据
        String serviceName = "nonexistentService";
        java.util.Map<String, Object> parameters = java.util.Map.of("param1", "value1");

        // 执行测试 - 这将抛出异常，因为服务不存在
        assertThrows(RuntimeException.class, () -> {
            driverFramework.executeServiceCall(serviceName, parameters);
        });
    }
}