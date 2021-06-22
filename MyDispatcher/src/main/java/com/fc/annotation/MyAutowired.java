package com.fc.annotation;

import java.lang.annotation.*;

/**
 * 自定义@Autowired注解
 *
 * @author juice
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
