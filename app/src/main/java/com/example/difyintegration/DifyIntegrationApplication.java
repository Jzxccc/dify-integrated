package com.example.difyintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DifyIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(DifyIntegrationApplication.class, args);
    }
}