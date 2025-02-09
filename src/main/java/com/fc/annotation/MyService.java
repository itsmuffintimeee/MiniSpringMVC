package com.fc.annotation;

import java.lang.annotation.*;

/**
 * 自定义@Service注解
 *
 * @author juice
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {
    String value() default "";
}
