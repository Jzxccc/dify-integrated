package com.example.difyintegration.config;

import com.example.difyintegration.service.AIServiceRegistry;
import com.example.difyintegration.util.AIServiceSchemaGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * AI服务Schema落地配置
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AISchemaPersistenceConfig {

    private final AIServiceRegistry aiServiceRegistry;
    private final AIServiceSchemaGenerator schemaGenerator;

    private static final String SCHEMA_OUTPUT_DIR = "generated-schemas";

    @EventListener(ApplicationReadyEvent.class)
    public void persistSchemasToFile() {
        try {
            // 创建输出目录
            Path outputPath = Paths.get(SCHEMA_OUTPUT_DIR);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            // 获取所有服务并生成Schema，只处理带有AIService注解且需要公开的服务
            for (AIServiceRegistry.ServiceInfo serviceInfo : aiServiceRegistry.getAllServices()) {
                // 检查方法是否带有AIService注解
                if (serviceInfo.getAIServiceAnnotation() != null) {
                    String serviceName = serviceInfo.getAIServiceAnnotation().name().isEmpty()
                        ? serviceInfo.getMethod().getName()
                        : serviceInfo.getAIServiceAnnotation().name();

                    // 生成Schema
                    String schema = schemaGenerator.generateSchema(serviceInfo.getMethod());

                    // 写入文件
                    Path filePath = outputPath.resolve(serviceName + ".json");
                    try (FileWriter writer = new FileWriter(filePath.toFile())) {
                        writer.write(schema);
                        log.info("Schema persisted to: {}", filePath.toAbsolutePath());
                    }
                } else {
                    log.debug("Skipping service without AIService annotation: {}", serviceInfo.getMethod().getName());
                }
            }

            log.info("All AI service schemas have been persisted to {}", outputPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error persisting AI service schemas to file", e);
        }
    }
}