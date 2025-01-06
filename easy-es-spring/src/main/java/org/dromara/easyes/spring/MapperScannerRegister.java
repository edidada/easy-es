package org.dromara.easyes.spring;

import org.dromara.easyes.common.utils.EEVersionUtils;
import org.dromara.easyes.common.utils.LogUtils;
import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.dromara.easyes.common.constants.BaseEsConstants.*;

/**
 * 注册bean
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
public class MapperScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    private static final String BASE_PACKAGE = "basePackage";
    private static final String INDEX_STRATEGY_FACTORY = "indexStrategyFactory";
    private static final String REST_HIGH_LEVEL_CLIENT_UTILS = "restHighLevelClientUtils";
    private static final String REST_HIGH_LEVEL_CLIENT_CONFIGURATION = "restHighLevelClientConfiguration";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {


        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EsMapperScan.class.getName()));
        // 默认已注解标记为主，如果没有则尝试寻找easy-es配置 scan
        if (mapperScanAttrs != null) {
            List<String> basePackages = Arrays.stream(mapperScanAttrs.getStringArray("value"))
                    .filter(StringUtils::hasText).collect(Collectors.toList());
            // 注册bean
            registerBeanDefinitions(registry, generateBaseBeanName(importingClassMetadata, 0),
                    StringUtils.toStringArray(basePackages));
        }
    }

    /**
     * 通过BeanDefinition注册bean，在bean注册前处理interface中bean参数问题，interface与普通bean不同，
     * 使用了jdk动态代理，需要确定注册的实际interface class，就需要通过BeanDefinition来追加属性，
     * 当前使用到了spring的构造
     * @param registry spring bean扫码注册器
     * @param basePackages 扫码的包
     */
    void registerBeanDefinitions(BeanDefinitionRegistry registry, String beanName, String... basePackages) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue(BASE_PACKAGE, String.join(",", basePackages));
        if (registry.containsBeanDefinition(INDEX_STRATEGY_FACTORY)) {
            builder.addPropertyValue(INDEX_STRATEGY_FACTORY, registry.getBeanDefinition(INDEX_STRATEGY_FACTORY));
        }
        // 如果自行注册了restHighLevelClientUtils，那么就添加bean属性
        if (registry.containsBeanDefinition(REST_HIGH_LEVEL_CLIENT_UTILS)) {
            builder.addPropertyValue(REST_HIGH_LEVEL_CLIENT_UTILS, registry
                    .getBeanDefinition(REST_HIGH_LEVEL_CLIENT_UTILS));
        } else {
            // 没有注册restHighLevelClientUtils，那么尝试寻找easy-es-spring的restHighLevelClientConfiguration
            if (registry.containsBeanDefinition(REST_HIGH_LEVEL_CLIENT_CONFIGURATION)) {
                builder.addPropertyValue(REST_HIGH_LEVEL_CLIENT_UTILS, registry
                        .getBeanDefinition(REST_HIGH_LEVEL_CLIENT_CONFIGURATION));
            }
        }

        // for spring-native
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + MapperScannerRegister.class.getSimpleName() + "#" + index;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
