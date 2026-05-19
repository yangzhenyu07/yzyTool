package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.annotation.CommonLog;
import org.example.entity.TUserTest;
import org.example.exception.ExceptionEnum;
import org.example.exception.model.ResponseResult;
import org.example.exception.throwtype.RunException;
import org.example.service.TUserTestService;
import org.example.vo.TUserTestVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 双层缓存测试 前端控制器
 * </p>
 *
 * @author yangzhenyu
 * @since 2026-05-14 11:52:16
 */

@Api(value = "双层缓存测试", tags = {" 双层缓存测试"})
@Slf4j
@Validated
@RestController
@RequestMapping(value="api/tUserTest")
public class TUserTestController {
    @Resource
    private TUserTestService tUserTestService;

    /**
     * 根据 code 查询
     *
     * GET /tUserTest/{code}
     */
    @GetMapping("/{code}")
    public ResponseResult getById(@PathVariable("code") String code) {
        if (StringUtils.isEmpty(code)){
            throw new RunException(ExceptionEnum.ERROR_MSG, "code不能为空");
        }
        return ResponseResult.ok(tUserTestService.getById(code));
    }

    /**
     * 新增
     *
     * POST /tUserTest
     */
    @ApiOperation(value = "新增", notes = "新增")
    @CommonLog(methodName = "新增",className = "ToolController#scan" ,url = "api/tUserTest/save")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseResult save(@Validated @RequestBody TUserTestVo user) {
        int rows = tUserTestService.save(user);

        if (rows > 0) {
            return ResponseResult.ok("新增成功");
        }

        throw new RunException(ExceptionEnum.ERROR_MSG, "新增失败");

    }

    /**
     * 更新
     *
     * PUT /tUserTest
     */
    @ApiOperation(value = "update", notes = "update")
    @CommonLog(methodName = "update",className = "update" ,url = "api/tUserTest/update")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@Validated @RequestBody TUserTestVo user) {
        int rows = tUserTestService.update(user);

        if (rows > 0) {
            return ResponseResult.ok("更新成功");
        }

        throw new RunException(ExceptionEnum.ERROR_MSG, "更新失败");

    }

    /**
     * 删除
     *
     * DELETE /tUserTest/{code}
     */
    @CommonLog(methodName = "删除",className = "删除" ,url = "-")
    @DeleteMapping("/{code}")
    public ResponseResult delete(@PathVariable("code") String code) {
        if (StringUtils.isEmpty(code)){
            throw new RunException(ExceptionEnum.ERROR_MSG, "code不能为空");
        }
        int rows = tUserTestService.delete(code);

        if (rows > 0) {
            return ResponseResult.ok("删除成功");

        }
        throw new RunException(ExceptionEnum.ERROR_MSG, "删除失败");
    }
}

