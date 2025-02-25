package org.dromara.easyes.core.cache;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.common.utils.jackson.JacksonCustomConfig;
import org.dromara.easyes.core.biz.EntityFieldInfo;
import org.dromara.easyes.core.biz.EntityInfo;
import org.dromara.easyes.core.toolkit.FieldUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * jackson缓存
 *
 * @author jaime
 * @version 1.0
 * @since 2025/2/24
 */
public class JacksonCache {

    public static void init(Map<Class<?>, EntityInfo> entityInfoMap) {
        if (entityInfoMap.isEmpty()) {
            return;
        }
        entityInfoMap.forEach((clz,e) -> {
            // 当前类
            if (!JacksonCustomConfig.jacksonConfigMap.containsKey(clz)) {
                JacksonCustomConfig config = init(clz, e.getMappingColumnMap(), e.getClassDateFormatMap().get(clz), e.getFieldList());
                JacksonCustomConfig.jacksonConfigMap.put(clz, config);
            }

            // nested嵌套类
            e.getNestedOrObjectClassMappingColumnMap().forEach((nestedClz, nestedMappingColumnMap) -> {
                if (JacksonCustomConfig.jacksonConfigMap.containsKey(clz)) {
                    return;
                }
                JacksonCustomConfig c = init(nestedClz, nestedMappingColumnMap,
                        e.getClassDateFormatMap().get(nestedClz),
                        e.getNestedOrObjectFieldListMap().get(nestedClz)
                );
                JacksonCustomConfig.jacksonConfigMap.put(nestedClz, c);
            });
        });
    }

    private static JacksonCustomConfig init(
            Class<?> clz,
            Map<String, String> mappingColumnMap,
            Map<String, String> formatMap,
            List<EntityFieldInfo> fieldList
    ) {
        JacksonCustomConfig config = new JacksonCustomConfig();
        config.clz = clz;
        if (mappingColumnMap != null && !mappingColumnMap.isEmpty()) {
            config.javaJsonFieldNameMap.putAll(mappingColumnMap);
            config.allJsonField = JsonIncludeProperties.Value.from(new JsonIncludeProperties() {
                @Override
                public String[] value() {
                    return config.javaJsonFieldNameMap.values().toArray(new String[0]);
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonIncludeProperties.class;
                }
            });
        }

        // java字段名 - 格式化
        if (formatMap != null && !formatMap.isEmpty()) {
            formatMap.forEach((javaFieldName, format) -> {
                String getterName = getterMethod(clz, javaFieldName);
                if (getterName != null) {
                    config.formatMap.put(getterName, JsonFormat.Value.forPattern(format));
                }

                String setterName = setterMethod(clz, javaFieldName);
                if (setterName != null) {
                    config.formatMap.put(setterName, JsonFormat.Value.forPattern(format));
                }
            });
        }

        fieldList.forEach(f -> {
            String getterName = getterMethod(clz, f.getColumn());
            if (getterName == null) {
                return;
            }
            switch (f.getFieldStrategy()) {
                case NOT_NULL:
                    JsonInclude.Value v = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, null);
                    config.includeMap.put(getterName, v);
                    config.includeMap.put(f.getColumn(), v);
                    break;
                case NOT_EMPTY:
                    JsonInclude.Value j = JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null);
                    config.includeMap.put(getterName, j);
                    config.includeMap.put(f.getColumn(), j);
                    break;
                default:
                    break;
            }
        });
        return config;
    }

    private static <V> String getterMethod(Class<V> clz, String fieldName) {
        String getterName1 = BaseEsConstants.GET_FUNC_PREFIX + FieldUtils.firstToUpperCase(fieldName);
        String getterName2 = BaseEsConstants.GET_FUNC_PREFIX + FieldUtils.firstToUpperCase(fieldName);
        return Arrays.stream(ClassUtil.getClassMethods(clz))
                .filter(m -> m.getName().equals(getterName1) || m.getName().equals(getterName2))
                .findFirst()
                .map(Method::getName)
                .orElse(null);
    }

    private static <V> String setterMethod(Class<V> clz, String fieldName) {
        String setterName1 = BaseEsConstants.SET_FUNC_PREFIX + FieldUtils.firstToUpperCase(fieldName);
        return Arrays.stream(ClassUtil.getClassMethods(clz))
                .filter(m -> m.getName().equals(setterName1))
                .findFirst()
                .map(Method::getName)
                .orElse(null);
    }
}
