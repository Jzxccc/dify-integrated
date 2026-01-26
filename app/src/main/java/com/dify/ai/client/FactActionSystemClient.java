package com.dify.ai.client;

import com.dify.ai.domain.model.Action;
import com.dify.ai.domain.model.FactsState;
import com.dify.ai.service.CompletePipelineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 一个简单的客户端，用于演示事实绑定和动作执行系统的使用
 */
@Component
public class FactActionSystemClient {

    private final CompletePipelineService completePipelineService;

    public FactActionSystemClient(CompletePipelineService completePipelineService) {
        this.completePipelineService = completePipelineService;
    }

    /**
     * 通过完整管道处理用户输入
     *
     * @param userInput 要处理的用户输入
     * @param availableActions 系统中可用的动作列表
     * @return 处理后的事实状态
     */
    public FactsState processUserInput(String userInput, List<Action> availableActions) {
        System.out.println("Processing user input: " + userInput);
        
        // Process through the complete pipeline
        FactsState result = completePipelineService.processThroughCompletePipeline(userInput, availableActions);
        
        System.out.println("Processing completed. Facts state contains " + 
                          result.getFacts().size() + " fact categories.");
        
        return result;
    }

    /**
     * Demonstrates the system with a sample input
     */
    public void demonstrateSystem() {
        System.out.println("\n=== Fact-Binding and Action Execution System Demo ===");
        
        // Example user input
        String userInput = "I want to check the status of order ORD12345 for supplier ABC Corp";
        
        // In a real implementation, you would load actions from configuration
        // For this demo, we'll create a simple action list
        List<Action> actions = List.of(
            Action.builder()
                .actionId("GET_ORDER_STATUS")
                .name("Get Order Status")
                .description("Retrieves the current status of an order")
                .requires(Set.of("order.id"))
                .produces(Set.of("order.status"))
                .build(),
            Action.builder()
                .actionId("GET_SUPPLIER_INFO")
                .name("Get Supplier Information")
                .description("Retrieves detailed information about a supplier")
                .requires(Set.of("supplier.name"))
                .produces(Set.of("supplier.info"))
                .build()
        );
        
        FactsState result = processUserInput(userInput, actions);
        
        System.out.println("Demo completed successfully!");
    }
}