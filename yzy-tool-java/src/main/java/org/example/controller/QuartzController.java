package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.annotation.CommonLog;
import org.example.exception.model.ResponseResult;
import org.example.util.QuartzJobUtil;
import org.example.util.vo.TaskBaseInfo;
import org.example.vo.QuartzReqVo;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
* @author 杨镇宇
* @date 2025/9/8 13:51
* @version 1.0
*/
@Api(value = "定时器控制类", tags = {"定时器控制类"})
@Slf4j
@Validated
@RestController
@RequestMapping(value="api/quartz")
public class QuartzController {
    //注入任务调度
    @Qualifier("schedulerFactoryBean")
    @Autowired
    private Scheduler scheduler;
    @ApiOperation(value = "创建任务", notes = "创建任务")
    @CommonLog(methodName = "创建任务",className = "QuartzController#createJob" ,url = "api/quartz/createJob")
    @RequestMapping(value = "/createJob", method = RequestMethod.POST)
    public ResponseResult createJob(@Validated @RequestBody QuartzReqVo quartzReqVo){
        Date startTime = new Date(System.currentTimeMillis() + 5000); // 5秒后
        Date endTime = new Date(System.currentTimeMillis() + (60000 * 30)); // 30分后

        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobType(quartzReqVo.getJobType())
                .startTime(startTime) // 5s后立即执行
                .endTime(endTime)  //30s后定时任务失效
                .taskCron(quartzReqVo.getTaskCron())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.createJob(scheduler,quartzBean);

        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }
    @ApiOperation(value = "更新任务", notes = "更新任务")
    @CommonLog(methodName = "更新任务",className = "QuartzController#updateJob" ,url = "api/quartz/updateJob")
    @RequestMapping(value = "/updateJob", method = RequestMethod.POST)
    public ResponseResult updateJob(@Validated @RequestBody QuartzReqVo quartzReqVo){
        Date startTime = new Date(System.currentTimeMillis() + 5000); // 5秒后
        Date endTime = new Date(System.currentTimeMillis() + 30000); // 30秒后

        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobType(quartzReqVo.getJobType())
                .startTime(startTime) // 5s后立即执行
                .endTime(endTime)  //30s后定时任务失效
                .taskCron(quartzReqVo.getTaskCron())
                .endTime(quartzReqVo.getEndTime())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.updateJob(scheduler,quartzBean);

        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }
    @ApiOperation(value = "立即执行任务", notes = "立即执行任务")
    @CommonLog(methodName = "立即执行任务",className = "QuartzController#runJobNow" ,url = "api/quartz/runJobNow")
    @RequestMapping(value = "/runJobNow", method = RequestMethod.POST)
    public ResponseResult runJobNow(@Validated @RequestBody QuartzReqVo quartzReqVo){
        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobType(quartzReqVo.getJobType())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.runJobNow(scheduler,quartzBean);
        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }
    @ApiOperation(value = "暂停任务", notes = "暂停任务")
    @CommonLog(methodName = "暂停任务",className = "QuartzController#pauseScheduleJob" ,url = "api/quartz/pauseScheduleJob")
    @RequestMapping(value = "/pauseScheduleJob", method = RequestMethod.POST)
    public ResponseResult pauseScheduleJob(@Validated @RequestBody QuartzReqVo quartzReqVo){
        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobType(quartzReqVo.getJobType())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.pauseScheduleJob(scheduler,quartzBean);

        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }

    @ApiOperation(value = "恢复任务", notes = "恢复任务")
    @CommonLog(methodName = "恢复任务",className = "QuartzController#resumeScheduleJob" ,url = "api/quartz/resumeScheduleJob")
    @RequestMapping(value = "/resumeScheduleJob", method = RequestMethod.POST)
    public ResponseResult resumeScheduleJob(@Validated @RequestBody QuartzReqVo quartzReqVo){

        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobType(quartzReqVo.getJobType())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.resumeScheduleJob(scheduler,quartzBean);
        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }



    @ApiOperation(value = "删除任务", notes = "删除任务")
    @CommonLog(methodName = "删除任务",className = "QuartzController#deleteJob" ,url = "api/quartz/deleteJob")
    @RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
    public ResponseResult deleteJob(@Validated @RequestBody QuartzReqVo quartzReqVo){
        TaskBaseInfo quartzBean = TaskBaseInfo.builder()
                .quartzName(quartzReqVo.getQuartzName())
                .jobGroup(quartzReqVo.getJobGroup()) // 任务组
                .build();
        QuartzJobUtil.deleteJob(scheduler,quartzBean);

        return ResponseResult.ok(quartzReqVo.getQuartzName());

    }
}
