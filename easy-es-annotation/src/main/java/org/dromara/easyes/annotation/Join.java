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
@Target(ElementType.TYPE)
public @interface Join {
    String joinField() default "joinField";
    String rootAlias() default "";

    Child[] children() default {};
}
