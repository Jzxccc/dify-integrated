package com.example.difyintegration.llm;

import com.example.difyintegration.service.AIServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 意图解析器，将自然语言转换为服务调用请求
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IntentParser {

    private final AIServiceRegistry aiServiceRegistry;

    /**
     * 解析用户输入的意图
     */
    public ParsedIntent parseIntent(String userInput) {
        log.debug("Parsing intent from user input: {}", userInput);
        
        // 这里可以实现更复杂的意图解析逻辑
        // 目前使用简单的关键词匹配作为示例
        String normalizedInput = userInput.toLowerCase().trim();
        
        // 提取参数（示例实现，实际可能需要更复杂的NLP处理）
        java.util.Map<String, Object> extractedParams = extractParameters(normalizedInput);
        
        return new ParsedIntent(normalizedInput, extractedParams);
    }

    /**
     * 从用户输入中提取参数
     */
    private java.util.Map<String, Object> extractParameters(String input) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        
        // 示例：从输入中提取简单的参数
        // 在实际实现中，这可能涉及更复杂的NLP处理
        if (input.contains("app") && input.contains("=")) {
            // 简单的正则表达式提取参数
            Pattern pattern = Pattern.compile("app\\s*=\\s*([\\w\\-]+)");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                params.put("appId", matcher.group(1));
            }
        }
        
        if (input.contains("user") && input.contains("=")) {
            Pattern pattern = Pattern.compile("user\\s*=\\s*([\\w\\-]+)");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                params.put("userId", matcher.group(1));
            }
        }
        
        if (input.contains("conversation") && input.contains("=")) {
            Pattern pattern = Pattern.compile("conversation\\s*=\\s*([\\w\\-]+)");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                params.put("conversationId", matcher.group(1));
            }
        }
        
        // 可以根据需要添加更多参数提取规则
        params.put("query", input); // 默认将整个输入作为查询
        
        return params;
    }

    /**
     * 解析后的意图类
     */
    public static class ParsedIntent {
        private final String originalInput;
        private final java.util.Map<String, Object> parameters;

        public ParsedIntent(String originalInput, java.util.Map<String, Object> parameters) {
            this.originalInput = originalInput;
            this.parameters = parameters;
        }

        public String getOriginalInput() {
            return originalInput;
        }

        public java.util.Map<String, Object> getParameters() {
            return parameters;
        }
    }
}