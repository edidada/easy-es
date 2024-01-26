package cn.easyes.annotation;

import java.lang.annotation.*;


/**
 * @author lyy
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsDS {

    String value();
}
