package org.dromara.easyes.starter;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.dromara.easyes.common.enums.SchemaEnum;
import org.dromara.easyes.common.utils.*;
import org.dromara.easyes.core.service.AutoProcessIndexService;
import org.dromara.easyes.spring.factory.IndexStrategyFactory;
import org.dromara.easyes.spring.index.AutoProcessIndexNotSmoothlyServiceImpl;
import org.dromara.easyes.spring.index.AutoProcessIndexSmoothlyServiceImpl;
import org.dromara.easyes.common.property.EasyEsDynamicProperties;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.util.*;

import static org.dromara.easyes.common.constants.BaseEsConstants.*;

/**
 * es自动配置
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Configuration
@ConditionalOnClass(RestHighLevelClient.class)
@ConditionalOnExpression("'${easy-es.address:x}'!='x'")
@ConditionalOnProperty(prefix = "easy-es", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class EsAutoConfiguration {

    /**
     * 装配RestHighLevelClient
     *
     * @return RestHighLevelClient bean
     */
    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient() {
        return RestHighLevelClientUtils.restHighLevelClient(easyEsProperties());
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

    @Bean
    public IndexStrategyFactory indexStrategyFactory() {
        return new IndexStrategyFactory();
    }

    @Bean
    public AutoProcessIndexService autoProcessIndexSmoothlyServiceImpl() {
        return new AutoProcessIndexSmoothlyServiceImpl();
    }

    @Bean
    public AutoProcessIndexService autoProcessIndexNotSmoothlyServiceImpl() {
        return new AutoProcessIndexNotSmoothlyServiceImpl();
    }

}
