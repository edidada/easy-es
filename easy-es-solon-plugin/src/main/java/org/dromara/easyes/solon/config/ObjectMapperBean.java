package org.dromara.easyes.solon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.utils.jackson.EEObjectMapperBuilderCustomizer;
import org.dromara.easyes.common.utils.jackson.JsonUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * ObjectMapper是一个大概率会被使用的bean，所以将其封装一层，不直接将ObjectMapper注入容器
 */
@Component
public class ObjectMapperBean {
    @Getter
    private final ObjectMapper objectMapper;

    public ObjectMapperBean(@Inject(required = false) EEObjectMapperBuilderCustomizer customizer,
                            @Inject(value = "${easy-es}", autoRefreshed = true) EasyEsProperties easyEsProperties) {
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
