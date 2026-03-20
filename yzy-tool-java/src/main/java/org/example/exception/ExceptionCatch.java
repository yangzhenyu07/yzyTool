package org.example.exception;


import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.model.ResponseResult;
import org.example.exception.throwtype.RunException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @description: TODO 统一异常捕获类
* @author 杨镇宇
* @date 2022/4/12 11:20
* @version 1.0
*/
@ControllerAdvice
public class ExceptionCatch {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    //捕获CustomException此类异常
    @ExceptionHandler(RunException.class)
    @ResponseBody
    public ResponseResult customException(RunException customException){
        customException.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
    //捕获Exception此类异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        exception.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",exception.getMessage());
        if(EXCEPTIONS == null){
            EXCEPTIONS = builder.build();//EXCEPTIONS构建成功
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        //拦截json格式请求的参数校验异常
        if (BindException.class.isAssignableFrom(exception.getClass())){
            return setBindException(exception,resultCode);
        }

        //拦截普通表单格式的参数校验异常
        if (ConstraintViolationException.class.isAssignableFrom(exception.getClass())){
            return setConstraintViolationException(exception,resultCode);
        }
        //MethodArgumentNotValidException
        if (MethodArgumentNotValidException.class.isAssignableFrom(exception.getClass())){
            return setMethodArgumentNotValidException(exception,resultCode);
        }
        if(resultCode !=null){
            if (!StringUtils.isEmpty(exception.getMessage())) {
                resultCode.setDesc(exception.getMessage());
            }
            return new ResponseResult(resultCode);
        }else{
            //返回99999异常
            return new ResponseResult(ExceptionEnum.ERROR);
        }


    }

    public ResponseResult setMethodArgumentNotValidException(Throwable clazz,ResultCode resultCode){
        MethodArgumentNotValidException mne = (MethodArgumentNotValidException) clazz;
        StringBuilder sb = new StringBuilder();
        List<ObjectError> allErrors = mne.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        resultCode.setDesc(message);
        return new ResponseResult(resultCode);
    }
    public ResponseResult setConstraintViolationException(Throwable clazz,ResultCode resultCode){
        ConstraintViolationException cve = (ConstraintViolationException) clazz;
        Set<ConstraintViolation<?>> set = cve.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> e : set) {
            errorMsg.append(e.getMessage() + ";");
        }
        resultCode.setDesc(errorMsg.toString());
        return new ResponseResult(resultCode);
    }
    public ResponseResult setBindException( Throwable clazz,ResultCode resultCode){
        BindException bin = (BindException) clazz;
        List<ObjectError> errors =bin.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            ObjectError x = errors.get(i);
            if (i == (errors.size() - 1)) {
                errorMsg.append(x.getDefaultMessage());
            } else {
                errorMsg.append(x.getDefaultMessage() + ";");
            }
        }
        resultCode.setDesc(errorMsg.toString());
        return new ResponseResult(resultCode);
    }
    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,ExceptionEnum.INVALID_PARAM);
        builder.put(ConstraintViolationException.class, ExceptionEnum.INVALID_PARAM);
        builder.put(BindException.class, ExceptionEnum.INVALID_PARAM);
        builder.put(MissingServletRequestParameterException.class, ExceptionEnum.INVALID_PARAM);
        builder.put(MethodArgumentNotValidException.class, ExceptionEnum.INVALID_PARAM);
        builder.put(AccessDeniedException.class, ExceptionEnum.INSUFFICIENT_PERMISSIONS);
    }
}
