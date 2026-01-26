package com.dify.ai.runner;

import com.dify.ai.client.FactActionSystemClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * A startup runner to demonstrate the fact-binding and action execution system
 */
@Component
public class SystemDemoRunner implements CommandLineRunner {

    private final FactActionSystemClient factActionSystemClient;

    public SystemDemoRunner(FactActionSystemClient factActionSystemClient) {
        this.factActionSystemClient = factActionSystemClient;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run a demonstration of the system
        factActionSystemClient.demonstrateSystem();
    }
}