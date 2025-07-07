package org.dromara.easyes.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.utils.jackson.EEObjectMapperBuilderCustomizer;
import org.dromara.easyes.common.utils.jackson.JsonUtils;

/**
 * ObjectMapper是一个大概率会被使用的bean，所以将其封装一层，不直接将ObjectMapper注入容器
 */
public class ObjectMapperBean {
    @Getter
    private final ObjectMapper objectMapper;

    public ObjectMapperBean(EEObjectMapperBuilderCustomizer customizer,
                            EasyEsProperties easyEsProperties) {
        ObjectMapper ret;
        if (customizer != null) {
            ret = JsonUtils.buildJsonMapper(customizer);
        } else {
            String dateFormat = easyEsProperties.getGlobalConfig().getDbConfig().getDefaultDateFormat();
            ret = JsonUtils.buildJsonMapperWithDateFormat(dateFormat);
        }
        this.objectMapper = ret;
    }
}
