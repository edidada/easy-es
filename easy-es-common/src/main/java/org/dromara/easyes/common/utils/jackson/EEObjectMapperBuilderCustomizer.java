package org.dromara.easyes.common.utils.jackson;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.dromara.easyes.common.property.EasyEsProperties;

/**
 * 用户自定义Jackson配置
 * @see org.dromara.easyes.solon.config.EsAutoConfiguration#elasticClient(EasyEsProperties)
 * @see org.dromara.easyes.starter.EsAutoConfiguration#elasticClient()
 */
@FunctionalInterface
public interface EEObjectMapperBuilderCustomizer {
	void customize(JsonMapper.Builder builder);
}