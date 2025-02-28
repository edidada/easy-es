package org.dromara.easyes.starter;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.dromara.easyes.common.property.EasyEsDynamicProperties;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.utils.EsClientUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es自动配置
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Configuration
@ConditionalOnClass(ElasticsearchClient.class)
@ConditionalOnExpression("'${easy-es.address:x}'!='x'")
@ConditionalOnProperty(prefix = "easy-es", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class EsAutoConfiguration {

    /**
     * 装配ElasticsearchClient
     *
     * @return ElasticsearchClient bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchClient elasticClient() {
        return EsClientUtils.buildClient(easyEsProperties());
    }

    @Bean
    @ConfigurationProperties(prefix = "easy-es")
    public EasyEsProperties easyEsProperties() {
        return new EasyEsProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "easy-es.dynamic")
    public EasyEsDynamicProperties easyEsDynamicProperties() {
        return new EasyEsDynamicProperties();
    }

}
