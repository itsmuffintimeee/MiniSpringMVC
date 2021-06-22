package com.fc.annotation;

import java.lang.annotation.*;

/**
 * 自定义@RequestParam注解
 *
 * @author juice
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
