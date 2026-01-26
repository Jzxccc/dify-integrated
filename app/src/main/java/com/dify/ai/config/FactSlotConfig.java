package com.dify.ai.config;

import com.dify.ai.domain.model.FactSlot;
import com.dify.ai.service.FactSlotRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import jakarta.annotation.PostConstruct;
import java.util.Properties;

/**
 * 从属性文件加载事实槽定义的配置类
 */
@Configuration
@PropertySource("classpath:fact-slots.properties")
public class FactSlotConfig {

    private final FactSlotRegistry factSlotRegistry;

    public FactSlotConfig(FactSlotRegistry factSlotRegistry) {
        this.factSlotRegistry = factSlotRegistry;
    }

    @Value("${supplier.name.type:STRING}")
    private String supplierNameType;

    @Value("${supplier.name.description:Name of the supplier}")
    private String supplierNameDescription;

    @Value("${supplier.name.required:true}")
    private boolean supplierNameRequired;

    @Value("${supplier.name.multiValue:false}")
    private boolean supplierNameMultiValue;

    @Value("${supplier.id.type:STRING}")
    private String supplierIdType;

    @Value("${supplier.id.description:Unique identifier for the supplier}")
    private String supplierIdDescription;

    @Value("${supplier.id.required:true}")
    private boolean supplierIdRequired;

    @Value("${supplier.id.multiValue:false}")
    private boolean supplierIdMultiValue;

    @Value("${order.id.type:STRING}")
    private String orderIdType;

    @Value("${order.id.description:Identifier for the order}")
    private String orderIdDescription;

    @Value("${order.id.required:true}")
    private boolean orderIdRequired;

    @Value("${order.id.multiValue:false}")
    private boolean orderIdMultiValue;

    @Value("${product.id.type:STRING}")
    private String productIdType;

    @Value("${product.id.description:Identifier for the product}")
    private String productIdDescription;

    @Value("${product.id.required:true}")
    private boolean productIdRequired;

    @Value("${product.id.multiValue:false}")
    private boolean productIdMultiValue;

    @PostConstruct
    public void initializeFactSlots() {
        // Register supplier-related fact slots
        factSlotRegistry.registerFactSlot(FactSlot.builder()
                .factId("supplier.name")
                .type(supplierNameType)
                .description(supplierNameDescription)
                .required(supplierNameRequired)
                .multiValue(supplierNameMultiValue)
                .build());

        factSlotRegistry.registerFactSlot(FactSlot.builder()
                .factId("supplier.id")
                .type(supplierIdType)
                .description(supplierIdDescription)
                .required(supplierIdRequired)
                .multiValue(supplierIdMultiValue)
                .build());

        // Register order-related fact slots
        factSlotRegistry.registerFactSlot(FactSlot.builder()
                .factId("order.id")
                .type(orderIdType)
                .description(orderIdDescription)
                .required(orderIdRequired)
                .multiValue(orderIdMultiValue)
                .build());

        // Register product-related fact slots
        factSlotRegistry.registerFactSlot(FactSlot.builder()
                .factId("product.id")
                .type(productIdType)
                .description(productIdDescription)
                .required(productIdRequired)
                .multiValue(productIdMultiValue)
                .build());
    }
}