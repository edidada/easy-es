package org.dromara.easyes.test.entity;


import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;


/**
 * es 联系方式 数据模型 Author的子文档,Author是其父文档
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Data
public class Contact {
    /**
     * 手机号
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String phone;
    /**
     * 邮箱
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String email;
}
