package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.annotation.CommonLog;
import org.example.config.token.JxToken;
import org.example.config.token.ScToken;
import org.example.exception.model.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangzhenyu
 * */
@Api(value = "token生成服务", tags = {"token生成服务"})
@RestController
@RequestMapping(value="/api/token")
public class TokenController implements JxToken {


    @Resource
    private HttpServletRequest request;
    @Resource
    private ScToken scToken;
    @ApiOperation(value = "token生成接口", notes = "token生成接口")
    @CrossOrigin(origins = "*")
    @CommonLog(methodName = "token生成接口",className = "TokenController#init",url = "api/token/init")
    @GetMapping("/init")
    public ResponseResult init(@RequestParam(value = "name", defaultValue = "yangzhenyu") String name, @RequestParam(value = "age", defaultValue = "26") String age) {
        ScToken scToken = new ScToken();
        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("age",age);
        Map<String, Object> result = scToken.initToken(map);
        return ResponseResult.ok(result);
    }

    @ApiOperation(value = "解析token接口", notes = "解析token接口")
    @CrossOrigin(origins = "*")
    @CommonLog(methodName = "解析token接口",className = "TokenController#jx",url = "api/token/jx")
    @GetMapping("/jx")
    public ResponseResult jx() {
        Map<String, Object> info = getAccessInfo(request);
        return ResponseResult.ok(info);
    }


}
