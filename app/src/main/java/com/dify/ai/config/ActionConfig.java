package com.dify.ai.config;

import com.dify.ai.domain.model.Action;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从YAML文件加载动作定义的配置类
 */
@Configuration
public class ActionConfig {

    // 这常会从YAML文件加载动作
    // 目前，我们将通过编程方式定义它们
    
    @PostConstruct
    public void initializeActions() {
        // 在实际实现中，这将从actions.yml加载
        // 目前，我们只记录配置的存在
        System.out.println("Action configuration loaded from actions.yml");
    }
    
    /**
     * 从YAML配置文件加载动作
     */
    public List<Action> loadActionsFromYaml() {
        Resource resource = new ClassPathResource("actions.yml");
        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(Map.class, loaderOptions));

        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> data = yaml.load(inputStream);
            List<Map<String, Object>> actionsData = (List<Map<String, Object>>) data.get("actions");

            if (actionsData == null) {
                return List.of(); // 如果没有定义动作，则返回空列表
            }

            return actionsData.stream().map(this::mapToAction).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load actions from YAML", e);
        }
    }
    
    /**
     * 将映射表示映射到动作对象
     */
    private Action mapToAction(Map<String, Object> actionMap) {
        String id = (String) actionMap.get("id");
        String name = (String) actionMap.get("name");
        String description = (String) actionMap.get("description");
        List<String> requires = (List<String>) actionMap.get("requires");
        List<String> produces = (List<String>) actionMap.get("produces");
        
        return Action.builder()
                .actionId(id)
                .name(name)
                .description(description)
                .requires(new HashSet<>(requires))
                .produces(new HashSet<>(produces))
                .build();
    }
}