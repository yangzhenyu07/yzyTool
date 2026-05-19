package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.annotation.CommonLog;
import org.example.exception.model.ResponseResult;
import org.example.service.RedisService;
import org.example.vo.ScanVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* @author 杨镇宇
* @date 2025/2/17 11:05
* @version 1.0
*/
@Api(value = "redis 分布式", tags = {" redis 分布式"})
@Slf4j
@Validated
@RestController
@RequestMapping(value="api/redis")
public class RedisController {

    @Resource
    private RedisService redisService;

    @ApiOperation(value = "redis 分布式 测试", notes = "redis 分布式 测试")
    @CommonLog(methodName = "redis 分布式 测试",className = "RedisController#lock" ,url = "api/redis/lock")
    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    public ResponseResult lock(@Validated @RequestBody ScanVo scanVo) throws InterruptedException {
        boolean success = redisService.testLock(scanVo,scanVo.getPath());
        return ResponseResult.ok("success");
    }
}
