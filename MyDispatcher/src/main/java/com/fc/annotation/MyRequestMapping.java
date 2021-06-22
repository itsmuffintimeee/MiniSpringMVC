package com.fc.annotation;

import java.lang.annotation.*;

/**
 * 自定义@RequestMapping注解
 *
 * @author juice
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
