package org.example.config;

import brave.Tracer;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.util.IpUtil;
import org.example.vo.LogTracerVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
* @author 杨镇宇
* @date 2024/11/26 10:48
* @version 1.0
*/
@Component
@Slf4j
public class LogTracer {

    @Resource
    private Tracer tracer;

    public LogTracerVo getTrace(){
        String traceId = ObjectUtil.isNotNull(tracer.currentSpan()) ? Long.toHexString(tracer.currentSpan().context().traceId()) :"no traceId";
        String hostAddress = IpUtil.getIp();
        return LogTracerVo.builder().traceId(traceId).ip(hostAddress).build();
    }
}
