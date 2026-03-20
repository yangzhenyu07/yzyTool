package org.example.config.web3;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Spring AOP切面：交易异常增强（装饰器模式）
 * @author 杨镇宇
 * @date 2026/3/20 02:32
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
public class TransactionAopAdvice {
    /**
     * 交易异常日志记录
     */
    @AfterThrowing(pointcut = "execution(* org.example.config.web3.TransactionUtils.*(..))", throwing = "e")
    public void afterThrowing(Exception e) {
        // 可扩展：记录日志、发送告警、失败重试等
        log.error("交易操作异常：" ,e);
    }
}