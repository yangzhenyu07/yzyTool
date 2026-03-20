package org.example.annotation;

import java.lang.annotation.*;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2023/6/7 11:06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CommonLog {
    String methodName() default "";
    String className() default "";
    String url() default  "";
}
