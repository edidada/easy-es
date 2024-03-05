package org.dromara.easyes.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 父子类型
 * <p>
 * Copyright © 2024 xpc1024 All Rights Reserved
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface Child {
    /**
     * 父文档别名 非必填,默认值为当前类名小写
     *
     * @return 父文档别名
     */
    String parentAlias() default "";

    /**
     * 父文档实体类,必填项
     *
     * @return 父文档实体类
     */
    Class<?> parentClass();

    /**
     * 子文档别名列表
     *
     * @return 子文档别名列表 非必填,默认值为子文档类名小写
     */
    String[] childAliases() default {};

    /**
     * 子文档实体类列表,必填项
     *
     * @return 子文档实体类列表
     */
    Class<?>[] childClasses();
}
