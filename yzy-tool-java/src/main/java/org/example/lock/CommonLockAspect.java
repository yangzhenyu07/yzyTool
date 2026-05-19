package org.example.lock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.exception.ExceptionEnum;
import org.example.exception.throwtype.RunException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis锁实现
 * @author 杨镇宇
 * @date 2025/2/17 10:37
 * @version 1.1
 */
@Slf4j
@Aspect
@Component
public class CommonLockAspect {

    @Autowired
    private  RedisLockHelper redisLockHelper;


    @SneakyThrows
    @Around("@annotation(commonLock)")  // 监听 @CommonLock 注解
    public Object around(ProceedingJoinPoint joinPoint, CommonLock commonLock)  {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 解析 SpEL 表达式，生成动态的锁 key
        String key = parseSpEL(method, joinPoint.getArgs(), commonLock.key());
        String lockValue = UUID.randomUUID().toString();

        boolean locked = false;
        try {
            // 尝试获取分布式锁
            locked = tryLock(key, lockValue, commonLock.expire(),
                    commonLock.retryTimes(), commonLock.retryInterval());

            if (!locked) {
                throw  new RunException(ExceptionEnum.ERROR_MSG,commonLock.message());
            }

            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 执行完后，释放锁
            if (locked) {
                // 尝试释放锁，并处理失败情况
                boolean released = redisLockHelper.releaseLock(key, lockValue);
                if (!released) {
                    log.warn("Failed to release lock for key: " + key);
                }
            }
        }
    }

    private boolean tryLock(String key, String value, long expire,
                            int retryTimes, long retryInterval) {
        int remainingRetry = retryTimes;
        long backoffInterval = retryInterval;
        do {
            // 如果获取锁成功，返回 true
            if (redisLockHelper.tryLock(key, value, expire)) {
                return true;
            }
            // 如果锁没有成功获取，等待一段时间后重试
            if (backoffInterval > 0) {
                try {
                    // 指数退避策略：每次重试的间隔时间逐渐增加
                    TimeUnit.MILLISECONDS.sleep(backoffInterval);
                    backoffInterval *= 2; // 增加退避间隔
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } while (remainingRetry-- > 0);

        return false;  // 如果没有获取到锁，则返回 false
    }


    private String parseSpEL(Method method, Object[] args, String spEL) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        // 获取方法的参数名
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        // 解析 SpEL 表达式
        return parser.parseExpression(spEL).getValue(context, String.class);
    }
}
