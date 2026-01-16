package com.example.difyintegration.util;

import java.util.*;

// 能力定义
class Capability {
    String name;
    Set<String> inputs;
    Set<String> outputs;

    public Capability(String name, Set<String> inputs, Set<String> outputs) {
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    // 模拟执行方法
    public Map<String, Object> execute(Map<String, Object> inputData) {
        System.out.println("Executing " + name + " with input: " + inputData);
        Map<String, Object> outputData = new HashMap<>();
        for (String out : outputs) {
            // 模拟输出值
            outputData.put(out, out + "_value");
        }
        return outputData;
    }
}

// DAG 节点
class Node {
    Capability cap;
    Set<Node> dependsOn = new HashSet<>();
    public Node(Capability cap) { this.cap = cap; }
}

// Plan Compiler
class PlanCompiler {

    public List<Node> buildDAG(List<Capability> capabilities) {
        List<Node> nodes = new ArrayList<>();
        for (Capability cap : capabilities) nodes.add(new Node(cap));

        // 建立依赖关系
        for (Node current : nodes) {
            for (Node other : nodes) {
                if (current == other) continue;
                for (String input : current.cap.inputs) {
                    if (other.cap.outputs.contains(input)) {
                        current.dependsOn.add(other);
                    }
                }
            }
        }

        // 拓扑排序
        return topoSort(nodes);
    }

    private List<Node> topoSort(List<Node> nodes) {
        List<Node> sorted = new ArrayList<>();
        Set<Node> visited = new HashSet<>();
        Set<Node> visiting = new HashSet<>();

        for (Node node : nodes) {
            visit(node, visited, visiting, sorted);
        }

        return sorted;
    }

    private void visit(Node node, Set<Node> visited, Set<Node> visiting, List<Node> sorted) {
        if (visited.contains(node)) return;
        if (visiting.contains(node)) throw new RuntimeException("Cycle detected in DAG!");

        visiting.add(node);
        for (Node dep : node.dependsOn) visit(dep, visited, visiting, sorted);
        visiting.remove(node);
        visited.add(node);
        sorted.add(node);
    }
}

// 执行器
class Executor {
    private Map<String, Object> context = new HashMap<>();

    public void execute(List<Node> plan) {
        for (Node node : plan) {
            // 动态构造输入参数
            Map<String, Object> inputData = new HashMap<>();
            for (String input : node.cap.inputs) {
                // 动态绑定前一个能力输出
                if (context.containsKey(input)) {
                    inputData.put(input, context.get(input));
                } else {
                    inputData.put(input, input + "_default"); // 没有输出就用默认值
                }
            }

            // 执行能力
            Map<String, Object> outputData = node.cap.execute(inputData);

            // 保存输出到全局 context，用于下一个能力
            context.putAll(outputData);
        }
    }
}

// 测试
public class Main {
    public static void main(String[] args) {

        // 能力注册表（动态维护）
        Map<String, Capability> registry = new HashMap<>();
        registry.put("querySupplier", new Capability("querySupplier", Set.of("userInput"), Set.of("supplierOut")));
        registry.put("translate", new Capability("translate", Set.of("text"), Set.of("translateOut")));
        registry.put("notify", new Capability("notify", Set.of("translateOut"), Set.of()));

        // 用户意图识别结果（顺序可以乱）
        List<String> intents = List.of("translate", "notify", "querySupplier");

        // 从注册表获取能力对象
        List<Capability> capabilities = new ArrayList<>();
        for (String intent : intents) {
            Capability cap = registry.get(intent);
            if (cap != null) capabilities.add(cap);
        }

        // 构建执行计划
        PlanCompiler compiler = new PlanCompiler();
        List<Node> plan = compiler.buildDAG(capabilities);

        // 打印计划顺序
        System.out.println("Execution Plan Order:");
        for (Node node : plan) {
            System.out.println("- " + node.cap.name);
        }

        // 执行
        System.out.println("\nExecuting:");
        Executor executor = new Executor();

        // 核心动态字段绑定：querySupplier 输出 supplierOut 自动映射到 translate 的 text
        // Executor 内部通过 context 管理
        executor.execute(plan);
    }
}
