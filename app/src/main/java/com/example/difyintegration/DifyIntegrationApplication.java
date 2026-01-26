package com.example.difyintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.difyintegration", "com.dify.ai"})
@EnableScheduling
public class DifyIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DifyIntegrationApplication.class, args);
    }
}
