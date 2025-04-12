package org.dromara.easyes.solon.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.dromara.easyes.common.property.EasyEsDynamicProperties;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.strategy.AutoProcessIndexStrategy;
import org.dromara.easyes.common.utils.CollectionUtils;
import org.dromara.easyes.common.utils.EsClientUtils;
import org.dromara.easyes.core.index.AutoProcessIndexNotSmoothlyStrategy;
import org.dromara.easyes.core.index.AutoProcessIndexSmoothlyStrategy;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Map;

/**
 * es自动配置
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Configuration
@Condition(onClass = ElasticsearchClient.class, onProperty = "${easy-es.enable:true} = true")
public class EsAutoConfiguration {

    @Bean
    public EasyEsProperties easyEsProperties(
            @Inject(value = "${easy-es}", autoRefreshed = true) EasyEsProperties properties) {
        return properties;
    }

    @Bean
    public EasyEsDynamicProperties easyEsDynamicProperties(
            @Inject(value = "${easy-es.dynamic:}", autoRefreshed = true) EasyEsDynamicProperties dynamicProperties) {
        if (dynamicProperties == null) {
            return new EasyEsDynamicProperties();
        }
        return dynamicProperties;
    }

    /**
     * 装配ElasticsearchClient
     *
     * @return ElasticsearchClient bean
     */
    @Bean
    @Condition(onMissingBean = ElasticsearchClient.class)
    public ElasticsearchClient elasticClient(EasyEsProperties easyEsProperties) {
        return EsClientUtils.buildClient(easyEsProperties);
    }

    @Bean
    public EsClientUtils esClientUtils(EasyEsProperties properties, EasyEsDynamicProperties dynamicProperties) {
        EsClientUtils esClientUtils = new EsClientUtils();
        Map<String, EasyEsProperties> datasourceMap = dynamicProperties.getDatasource();
        if (CollectionUtils.isEmpty(datasourceMap)) {
            // 设置默认数据源,兼容不使用多数据源配置场景的老用户使用习惯
            datasourceMap.put(EsClientUtils.DEFAULT_DS, properties);
        }
        for (String key : datasourceMap.keySet()) {
            EasyEsProperties easyEsConfigProperties = datasourceMap.get(key);
            EsClientUtils.registerClient(key, () -> EsClientUtils.buildClient(easyEsConfigProperties));
        }
        return esClientUtils;
    }

    @Bean
    public AutoProcessIndexStrategy autoProcessIndexSmoothlyStrategy() {
        return new AutoProcessIndexSmoothlyStrategy();
    }

    @Bean
    public AutoProcessIndexStrategy autoProcessIndexNotSmoothlyStrategy() {
        return new AutoProcessIndexNotSmoothlyStrategy();
    }

}
