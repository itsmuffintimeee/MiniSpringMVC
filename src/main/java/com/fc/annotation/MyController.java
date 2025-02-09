package com.fc.annotation;

import java.lang.annotation.*;

/**
 * 自定义@Controller注解
 *
 * @author juice
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
