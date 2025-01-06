package org.dromara.easyes.test.config;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.dromara.easyes.spring.factory.IndexStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MoJie
 * @since 2.0
 */
//@Configuration
//@EsMapperScan("org.dromara.easyes.test.mapper")
public class TestConfig {

    @Bean
    public IndexStrategyFactory indexStrategyFactory() {
        return new IndexStrategyFactory();
    }

}
