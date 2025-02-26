package org.dromara.easyes.common.join;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
public class BaseJoin {

    public Map<String, Map<String, String>> joinFiled;

    @JsonAnyGetter
    public Map<String, Map<String, String>> getJoinField() {
        return joinFiled;
    }

    /**
     * 添加join字段信息. json示例如下:
     * "fieldName": {
     *     "name": xxx,
     *     "parentId": xxx
     * }
     *
     * @param fieldName join字段名称(json中的key)
     * @param name      名称
     * @param parentId  父id
     */
    public void addJoinField(String fieldName, String name, String parentId) {
        joinFiled = new HashMap<>() {{
            put(fieldName, new HashMap<>() {{
                put("name", name);
                if (parentId != null) {
                    put("parent", parentId);
                }
            }});
        }};
    }
}