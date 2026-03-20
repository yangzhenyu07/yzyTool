package org.example.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.annotation.CommonLog;
import org.example.config.LogTracer;
import org.example.exception.ExceptionEnum;
import org.example.exception.ResultCode;
import org.example.exception.model.ResponseResult;
import org.example.exception.throwtype.RunException;
import org.example.service.ToolService;
import org.example.vo.KeyWordVo;
import org.example.vo.LogTracerVo;
import org.example.vo.ScanVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
* @author 杨镇宇
* @date 2024/6/13 15:31
* @version 1.0
*/
@Api(value = "代码扫描", tags = {" 代码扫描"})
@Slf4j
@Validated
@RestController
@RequestMapping(value="api/tool")
public class ToolController {

    @Resource
    private ToolService toolService;
    @Resource
    private LogTracer logTracer;
    @ApiOperation(value = "扫描(html,js,vue),返回json", notes = "扫描(html,js,vue),返回json")
    @CommonLog(methodName = "扫描(html,js,vue),返回json",className = "ToolController#scan" ,url = "api/tool/scan")
    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    public ResponseResult scan(@Validated @RequestBody ScanVo scanVo){
        String check = scanVo.check();
        if (StringUtils.isNotBlank(check)){
            ResultCode r = ExceptionEnum.INVALID_PARAM;
            r.setDesc(check);
            throw  new RunException(r);
        }
        List<KeyWordVo> keyWordVos = toolService.scan(scanVo);
        if (CollectionUtils.isEmpty(keyWordVos)){
            return ResponseResult.ok(Lists.newArrayList());
        }


        return ResponseResult.ok(keyWordVos);
    }

    @ApiOperation(value = "扫描(html,js,vue)，汇总返回", notes = "扫描(html,js,vue)，汇总返回")
    @CommonLog(methodName = "扫描(html,js,vue)，汇总返回",className = "ToolController#scanText" ,url = "api/tool/scanText")
    @RequestMapping(value = "/scanText", method = RequestMethod.POST)
    public ResponseResult scanText(  @RequestBody ScanVo scanVo){
        String msg= "【文件路径】:{0},【行号】：{1},【扫描到的内容】:{2}";
        List<String> list = Lists.newArrayList();
        String check = scanVo.check();
        if (StringUtils.isNotBlank(check)){
            throw  new RunException(ExceptionEnum.INVALID_PARAM,check);
        }
        List<KeyWordVo> keyWordVos = toolService.scan(scanVo);
        if (CollectionUtils.isEmpty(keyWordVos)){
            return ResponseResult.ok(Lists.newArrayList());
        }
        keyWordVos.forEach(v->{
            list.add(MessageFormat.format(msg, v.getFilePath(),v.getLineNo(),v.getLine()));

        });
        return ResponseResult.ok(list);
    }

    @ApiOperation(value = "测试traceId", notes = "测试traceId")
    @CommonLog(methodName = "测试traceId",className = "ToolController#traceId",url = "api/tool/traceId")
    @GetMapping("/traceId")
    public ResponseResult traceId(){
        LogTracerVo trace = logTracer.getTrace();
        log.info("测试 traceId 与 ip ============================");
        log.info("traceID:"+trace.getTraceId());
        log.info("ip:"+trace.getIp());
        return ResponseResult.ok("测试traceId");

    }
}
