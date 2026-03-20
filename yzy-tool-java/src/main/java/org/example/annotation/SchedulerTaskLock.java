package org.example.annotation;

import org.example.annotation.constant.SchedulerTask;
import org.example.annotation.constant.SchedulerTaskEnum;

import java.lang.annotation.*;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/12/17 14:32
* @version 1.0
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SchedulerTaskLock {
    // 定时器 key
    SchedulerTaskEnum name() default SchedulerTaskEnum.DEFINED;

    // 持有缓存时间 5分钟
    long  lockAtMostForString()  default 300000;
    // 最大持有时间 防止死锁 默认10分钟
    long  lockMostTimeForString()  default 600000;

    // 存储定时信息 开关
    boolean saveLock() default false;
    //存储种类 默认只存储错误的
    String saveType() default SchedulerTask.ERROR;

    // 锁状态 true:单点串行状态,false:并行状态
    boolean lockStatus() default true;


}
