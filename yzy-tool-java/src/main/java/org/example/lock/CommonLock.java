package org.example.lock;

import java.lang.annotation.*;

/**
* @author 杨镇宇
* @date 2025/2/17 10:36
* @version 1.0
*/
@Target(ElementType.METHOD)                // 该注解只能用于方法
@Retention(RetentionPolicy.RUNTIME)       // 该注解在运行时可以通过反射访问
@Documented                               // 该注解会包含在 Javadoc 中
public @interface CommonLock {
    /**
     * 锁的key（支持SpEL表达式）
     */
    String key();

    /**
     * 锁的过期时间（秒）
     */
    long expire() default 30;

    /**
     * 获取锁失败时重试次数
     */
    int retryTimes() default 3;

    /**
     * 重试间隔（毫秒）
     */
    long retryInterval() default 1000;

    /**
     * 自定义错误信息
     */
    String message() default "系统繁忙，请稍后再试";
}
