package org.example.config;
import org.example.exception.model.ResponseResult;
import org.example.util.JsonUtils;
import org.example.vo.LogTracerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 统一返回数据拦截
 * */
@ControllerAdvice
@ConditionalOnWebApplication
public class DataResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static Logger log = LoggerFactory.getLogger(DataResponseBodyAdvice.class);
    @Resource
    private LogTracer logTracer;

    //白名单 url
    private static List<String> WHITE_LIST = Arrays.asList(
            "api/excel/export",
            "api/excel/getTest",
            "/api/view/export-page");

    @Override
    public Object beforeBodyWrite(Object object, MethodParameter parameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getURI().getPath();
        if (object instanceof ResponseResult) {
            log.info("请求url:{},返回数据:{}", path, JsonUtils.toJson(object));
        }else {
            log.info("请求url:{},返回数据:{}", path, object);
        }
        boolean flag = Boolean.TRUE;
        if (path.contains("doc.html") || path.contains("webjars") ||
                                        path.contains("swagger-ui") ||
                                        path.contains("swagger-resources") ||
                                        path.contains("error") ||
                                        path.contains("/v2/api-docs")){
            flag = Boolean.FALSE;

        }
        //白名单
        if (WHITE_LIST.contains(path)){
            flag = Boolean.FALSE;
        }
        if (flag) {
            LogTracerVo trace = logTracer.getTrace();
            if (object instanceof ResponseResult) {
                ResponseResult responseResult = (ResponseResult) object;
                responseResult.setIp(trace.getIp());
                responseResult.setTraceId(trace.getTraceId());
            }
            //response.getHeaders().add("YZY", "YZY");
        }
        return object;
    }

    @Override
    public boolean supports(MethodParameter parameter, Class<? extends HttpMessageConverter<?>> converter) {
        return true;
    }
    public DataResponseBodyAdvice() {
        log.info("===================统一返回数据拦截配置===================");
    }

}

