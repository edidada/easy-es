package org.dromara.easyes.starter;

import org.dromara.easyes.common.property.EasyEsDynamicProperties;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.strategy.AutoProcessIndexStrategy;
import org.dromara.easyes.common.utils.RestHighLevelClientUtils;
import org.dromara.easyes.core.index.AutoProcessIndexNotSmoothlyStrategy;
import org.dromara.easyes.core.index.AutoProcessIndexSmoothlyStrategy;
import org.dromara.easyes.spring.factory.IndexStrategyFactory;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
    public RestHighLevelClientUtils restHighLevelClientUtils() {
        RestHighLevelClientUtils restHighLevelClientUtils = new RestHighLevelClientUtils();
        Map<String, EasyEsProperties> datasourceMap = this.easyEsDynamicProperties().getDatasource();
        if (datasourceMap.isEmpty()) {
            // 设置默认数据源,兼容不使用多数据源配置场景的老用户使用习惯
            datasourceMap.put(RestHighLevelClientUtils.DEFAULT_DS, this.easyEsProperties());
        }
        for (String key : datasourceMap.keySet()) {
            EasyEsProperties easyEsConfigProperties = datasourceMap.get(key);
            RestHighLevelClientUtils.registerRestHighLevelClient(key, RestHighLevelClientUtils
                    .restHighLevelClient(easyEsConfigProperties));
        }
        return restHighLevelClientUtils;
    }

    /**
     * 索引策略注册
     * @return {@link AutoProcessIndexStrategy}
     * @author MoJie
     */
    @Bean
    public AutoProcessIndexStrategy autoProcessIndexSmoothlyStrategy() {
        return new AutoProcessIndexSmoothlyStrategy();
    }

    @Bean
    public AutoProcessIndexStrategy autoProcessIndexNotSmoothlyStrategy() {
        return new AutoProcessIndexNotSmoothlyStrategy();
    }

}
