package org.dromara.easyes.solon.config;

import lombok.Data;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lyy
 */
@Data
@Configuration
@Inject(value = "${easy-es.dynamic}", autoRefreshed = true)
public class DynamicEsProperties {

    /**
     * 配置多动态数据源key datasource id
     */
    private Map<String, EasyEsConfigProperties> datasource = new HashMap<>();
}