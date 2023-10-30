package org.dromara.easyes.test.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.HighLight;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.util.Set;

/**
 * es 嵌套类型
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@Data
@NoArgsConstructor
public class User {
    /**
     * 用户名
     */
    @IndexField(value = "user_name", fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART)
    @HighLight(mappingField = "highlightUsername")
    private String username;
    /**
     * 年龄
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer age;
    /**
     * 密码
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String password;
    /**
     * 多级嵌套
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = Faq.class)
    private Set<Faq> faqs;
    /**
     * 高亮显示的内容
     */
    private String highlightUsername;

    public User(String username, Integer age, String password, Set<Faq> faqs) {
        this.username = username;
        this.age = age;
        this.password = password;
        this.faqs = faqs;
    }
}
