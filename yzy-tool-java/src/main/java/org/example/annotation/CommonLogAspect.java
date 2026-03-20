package org.example.annotation;

import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2023/6/7 11:10
 */
@Aspect
@Component
@Slf4j
@Order(0)
public class CommonLogAspect {
    @SneakyThrows
    @Around("@annotation(commonLog)")
    public Object around(ProceedingJoinPoint joinPoint, CommonLog commonLog){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Set<String> parameter = new HashSet<>();
        String methodName = commonLog.methodName();
        String className = commonLog.className();
        String url = commonLog.url();
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = joinPoint.getArgs();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < parameters.length; ++i) {
            if ((parameterValues[i] instanceof List)) {
                parameter.addAll((ArrayList) parameterValues[i]);
            }else if (parameterValues[i] instanceof BeanPropertyBindingResult){

            }else {
                parameter.add(JSONUtil.toJsonStr(parameterValues[i]));
            }

        }
        log.info("start: <===【{}】--【{}】--【{}】===>访问  param:{}", url,StringUtils.isEmpty(methodName)?"-":methodName,
                                                      StringUtils.isEmpty(className)?"-":className,
                                                      parameter);
        Object proceed = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("end: <===【{}】--【{}】--【{}】===>访问  param:{},耗时:{}ms", url,StringUtils.isEmpty(methodName)?"-":methodName,
                                                        StringUtils.isEmpty(className)?"-":className,
                                                        parameter,
                                                        (endTime-startTime));

        return proceed;
    }


}
