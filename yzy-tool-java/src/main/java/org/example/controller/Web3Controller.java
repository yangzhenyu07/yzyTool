package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.annotation.CommonLog;
import org.example.exception.ExceptionEnum;
import org.example.exception.model.ResponseResult;
import org.example.exception.throwtype.RunException;
import org.example.service.Web3Service;
import org.example.vo.ScanVo;
import org.example.vo.Web3Vo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
* @author 杨镇宇
* @date 2026/3/20 23:28
* @version 1.0
*/
@Api(value = "Web3 链上交互", tags = {" Web3 链上交互"})
@Slf4j
@Validated
@RestController
@RequestMapping(value="api/web3")
public class Web3Controller {

    @Resource
    private Web3Service web3Service;

    @ApiOperation(value = "查询余额", notes = "查询余额")
    @CommonLog(methodName = "查询余额",className = "Web3Controller#query" ,url = "api/web3/query")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ResponseResult query( @RequestBody Web3Vo scanVo){
        if (StringUtils.isEmpty(scanVo.getAddress())){
            throw new RunException(ExceptionEnum.INVALID_PARAM, "地址不能为空");
        }
        return ResponseResult.ok(web3Service.query(scanVo));
    }

    @ApiOperation(value = "转账", notes = "转账")
    @CommonLog(methodName = "转账",className = "Web3Controller#transfer" ,url = "api/web3/transfer")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResponseResult transfer( @RequestBody Web3Vo scanVo){
        if (StringUtils.isEmpty(scanVo.getAddress())){
            throw new RunException(ExceptionEnum.INVALID_PARAM, "地址不能为空");
        }
        if (StringUtils.isEmpty(scanVo.getPrivateKey())){
            throw new RunException(ExceptionEnum.INVALID_PARAM, "账户不能为空");
        }
        BigDecimal amount = scanVo.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RunException(ExceptionEnum.INVALID_PARAM,"金额必须大于0");
        }
        return ResponseResult.ok(web3Service.transfer(scanVo));
    }
}
