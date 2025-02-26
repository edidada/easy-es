package org.dromara.easyes.common.utils.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.dromara.easyes.common.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON工具类
 *
 * @author jaime
 * @version 1.0
 * @since 2023/08/09
 */
public class JsonUtils {

    /**
     * 默认jackson配置
     */
    public static ObjectMapper OM_DEFAULT = base();
    private static final DefaultPrettyPrinter DEFAULT_PRETTY_PRINTER = new DefaultPrettyPrinter();

    public static JsonMapper base() {
        JsonMapper base = JsonMapper.builder()
                // 反序列化时是否将一个对象封装成单元素数组
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .build();
        AnnotationIntrospector anno = base.getSerializerProviderInstance().getAnnotationIntrospector();
        base.setAnnotationIntrospector(new AnnotationIntrospectorPair(anno, new JacksonCustomAnnotationIntrospector()));
        return base;
    }

    /**
     * 打印字符串
     *
     * @param data 数据
     * @return json字符串
     */
    public static String toJsonStr(Object data) {
        return toJsonStr(OM_DEFAULT, data);
    }

    /**
     * 字符数组
     *
     * @param data 数据
     * @return json字符串
     */
    public static byte[] toBytes(Object data) {
        return toBytes(OM_DEFAULT, data);
    }

    /**
     * 格式化打印字符串
     *
     * @param data 数据
     * @return json字符串
     */
    public static String toJsonPrettyStr(Object data) {
        return toJsonPrettyStr(OM_DEFAULT, data);
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(String json, Class<T> type) {
        return toBean(OM_DEFAULT, json, type);
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(String json, TypeReference<T> type) {
        return toBean(OM_DEFAULT, json, type);
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(String json, Type type) {
        return toBean(OM_DEFAULT, json, type);
    }

    /**
     * 字符串 ==》 List
     *
     * @param json 字符串
     * @param <V>  数据泛型
     * @return List
     */
    public static <V> List<V> toList(String json, Class<V> v) {
        return toList(OM_DEFAULT, json, v);
    }

    /**
     * 字符串 ==》 Set
     *
     * @param json 字符串
     * @param <V>  数据泛型
     * @return Set
     */
    public static <V> Set<V> toSet(String json, Class<V> v) {
        return toSet(OM_DEFAULT, json, v);
    }

    /**
     * 字符串 ==》 Map
     *
     * @param json 字符串
     * @param <K>  键泛型
     * @param <V>  值泛型
     * @return Map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> k, Class<V> v) {
        return toMap(OM_DEFAULT, json, k, v);
    }

    /**
     * 字符串 ==》 集合（Map, List, Set等）
     *
     * @param json             字符串
     * @param parametrized     主类型
     * @param parameterClasses 类型参数
     * @param <T>              集合泛型
     * @return 集合
     * @throws Exception 异常
     */
    public static <T> T toCollection(String json, Class<?> parametrized, Class<?>... parameterClasses) throws Exception {
        return toCollection(OM_DEFAULT, json, parametrized, parameterClasses);
    }

    /**
     * 字符串 ==》 JsonNode
     *
     * @param json 字符串
     * @return JsonNode
     */
    public static JsonNode readTree(String json) {
        return readTree(OM_DEFAULT, json);
    }

    /**
     * 打印字符串
     *
     * @param data 数据
     * @return json字符串
     */
    public static String toJsonStr(ObjectMapper om, Object data) {
        try {
            if (data == null) {
                return null;
            }
            return om.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符数组
     *
     * @param data 数据
     * @return json字符串
     */
    public static byte[] toBytes(ObjectMapper om, Object data) {
        try {
            if (data == null) {
                return null;
            }
            return om.writeValueAsBytes(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化打印字符串
     *
     * @param data 数据
     * @return json字符串
     */
    public static String toJsonPrettyStr(ObjectMapper om, Object data) {
        try {
            if (data == null) {
                return null;
            }
            return om.writer(DEFAULT_PRETTY_PRINTER).writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(ObjectMapper om, String json, Class<T> type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return om.readValue(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(ObjectMapper om, String json, TypeReference<T> type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return om.readValue(json, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 bean
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  数据泛型
     * @return bean
     */
    public static <T> T toBean(ObjectMapper om, String json, Type type) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return om.readValue(json, TypeFactory.defaultInstance().constructType(type));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 List
     *
     * @param json 字符串
     * @param <V>  数据泛型
     * @return List
     */
    public static <V> List<V> toList(ObjectMapper om, String json, Class<V> v) {
        try {
            return toCollection(om, json, List.class, v);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 Set
     *
     * @param json 字符串
     * @param <V>  数据泛型
     * @return Set
     */
    public static <V> Set<V> toSet(ObjectMapper om, String json, Class<V> v) {
        try {
            return toCollection(om, json, Set.class, v);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 Map
     *
     * @param json 字符串
     * @param <K>  键泛型
     * @param <V>  值泛型
     * @return Map
     */
    public static <K, V> Map<K, V> toMap(ObjectMapper om, String json, Class<K> k, Class<V> v) {
        try {
            return toCollection(om, json, Map.class, k, v);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串 ==》 集合（Map, List, Set等）
     *
     * @param json             字符串
     * @param parametrized     主类型
     * @param parameterClasses 类型参数
     * @param <T>              集合泛型
     * @return 集合
     * @throws Exception 异常
     */
    public static <T> T toCollection(ObjectMapper om, String json, Class<?> parametrized, Class<?>... parameterClasses) throws Exception {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        JavaType type = om.getTypeFactory().constructParametricType(parametrized, parameterClasses);
        return om.readValue(json, type);
    }

    /**
     * 字符串 ==》 JsonNode
     *
     * @param json 字符串
     * @return JsonNode
     */
    public static JsonNode readTree(ObjectMapper om, String json) {
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            return om.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
