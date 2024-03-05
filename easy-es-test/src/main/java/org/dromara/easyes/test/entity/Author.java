package org.dromara.easyes.test.entity;


import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;


/**
 * es 作者 数据模型 Document的子文档,Document是其父文档
 * <p>
 * Copyright © 2021 xpc1024 All Rights Reserved
 **/
@Data
public class Author {
    /**
     * 作者id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String authorId;
    /**
     * 作者姓名
     */
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_SMART)
    private String authorName;
}
