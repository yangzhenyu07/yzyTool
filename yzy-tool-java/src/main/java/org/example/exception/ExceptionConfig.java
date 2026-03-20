package org.example.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 异常参数配置
 * @author yangzhenyu
 * */
@Component
public class ExceptionConfig {
    /**
     * 默认提示code
     **/
    public static String code;
    /**
     * 默认提示错误信息
     **/
    public static String info;

    /******** 赋值数据 **********/


    @Value("${config.exception.code:999999}")
    private void setCode(String code) {
        ExceptionConfig.code = code;
    }

    @Value("${config.exception.info:系统繁忙,请稍后再试！}")
    private void setInfo(String info) {
        ExceptionConfig.info = info;
    }
}
