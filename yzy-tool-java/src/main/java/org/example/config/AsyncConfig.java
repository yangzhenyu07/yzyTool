package org.example.config;

import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
/**
* @author 杨镇宇
* @date 2025/1/21 15:50
* @version 1.0
*/

@Configuration
//启用异步支持：当你在配置类中添加 @EnableAsync 注解时，Spring 会启用异步功能，允许你在方法上使用 @Async 注解来指定这些方法需要异步执行。
@EnableAsync
public class AsyncConfig {

    /**
     * 配置线程池，用于处理异步任务
     * 可以在方法上使用 @Async 注解调用此线程池。
     * @return 线程池执行器
     */

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(25); // 队列容量
        executor.setThreadNamePrefix("Async-"); // 线程名前缀
        executor.initialize();
        return executor;
    }
}