package org.example.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.constant.SchedulerTask;
import org.example.annotation.constant.SchedulerTaskEnum;
import org.example.annotation.tag.SchedulerTaskUtils;
import org.example.annotation.tag.TagEnum;
import org.example.annotation.vo.LockHistoryVo;
import org.example.annotation.vo.TryLockVo;
import org.example.service.TSchedulerLockHistoryService;
import org.example.service.TSchedulerLockService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.sql.SQLException;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/12/17 14:32
* @version 1.0
*/
@Aspect
@Component
@Slf4j
@Order(1)
public class SchedulerTaskLockAspect {

    @Resource
    private TSchedulerLockService service;
    @Resource
    private TSchedulerLockHistoryService historyService;


    @Around("@annotation(schedulerTaskLock)")
    public Object around(ProceedingJoinPoint joinPoint, SchedulerTaskLock schedulerTaskLock) throws Throwable {
        //执行节点
        SchedulerTaskEnum schedulerTaskEnum = schedulerTaskLock.name();
        //持有缓存时间
        long lockAtMostFor = schedulerTaskLock.lockAtMostForString();
        //最大持有时间
        long lockMostTime = schedulerTaskLock.lockMostTimeForString();
        boolean status = schedulerTaskLock.saveLock();
        //存储种类
        String saveType = schedulerTaskLock.saveType();
        //锁状态 false 是无锁
        boolean lockStatus = schedulerTaskLock.lockStatus();
        boolean errorStatus = Boolean.FALSE;
        String message = null;
        Object proceed = null;
        String name = SchedulerTaskEnum.getKey(schedulerTaskEnum);

        TryLockVo tryLockVo = service.tryLock(name, lockAtMostFor, lockMostTime,lockStatus);
        if (tryLockVo.isStatus()) {
            try {
                // 成功获取锁，执行任务
                log.info("==========【{}】:schedulerTaskLock  start==================",name);
                long startTime = System.currentTimeMillis();
                try {
                    SchedulerTaskUtils.updateChannelTag(TagEnum.SUCCESS_TAG);
                    proceed = joinPoint.proceed();
                }catch (SQLException sq){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  sq);
                    errorStatus = Boolean.TRUE;
                    message = "【SQLException】-SQL异常 ";
                }catch (ArithmeticException ar){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  ar);
                    errorStatus = Boolean.TRUE;
                    message = "【ArithmeticException】-算术异常 ";
                }catch (UnknownHostException u){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  u);
                    errorStatus = Boolean.TRUE;
                    message = "【UnknownHostException】-无法确定主机IP异常 ";
                }catch (NullPointerException n){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  n);
                    errorStatus = Boolean.TRUE;
                    message = "【NullPointerException】-空指针异常 ";
                }catch (InterruptedException i){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  i);
                    errorStatus = Boolean.TRUE;
                    message = "【InterruptedException】-线程中断异常 ";
                }catch (FileNotFoundException f){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  f);
                    errorStatus = Boolean.TRUE;
                    message = "【FileNotFoundException】-文件未找到异常 ";
                }catch (ArrayIndexOutOfBoundsException  a){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  a);
                    errorStatus = Boolean.TRUE;
                    message = "【ArrayIndexOutOfBoundsException】-下标越界异常";
                }catch (ClassNotFoundException c){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  c);
                    errorStatus = Boolean.TRUE;
                    message = "【ClassNotFoundException】-未找到预装类异常";
                }catch (Exception e){
                    log.error("==========【"+name+"】schedulerTaskLock 任务执行失败，错误信息：",  e);
                    errorStatus = Boolean.TRUE;
                    message = "未知异常";
                }

                long endTime = System.currentTimeMillis();
                log.info("==========【{}】:schedulerTaskLock end  耗时:{}毫秒==================",name,(endTime-startTime));
                if (errorStatus && status && (SchedulerTask.ALL.equals(saveType) || SchedulerTask.ERROR.equals(saveType))) {
                    // 存储错误信息
                    LockHistoryVo build = LockHistoryVo.builder().id(null == tryLockVo.getId() ? "---" : tryLockVo.getId())
                            .keyName(name)
                            .keyValue(SchedulerTaskEnum.getValue(name))
                            .status(SchedulerTask.ERROR)
                            .message(message)
                            .build();
                    historyService.saveHistory(build);
                    return null;

                }
                if (status && (SchedulerTask.ALL.equals(saveType) || SchedulerTask.SUCCESS.equals(saveType))) {
                    // 存储任务执行成功的信息
                    LockHistoryVo build = LockHistoryVo.builder().id(null == tryLockVo.getId() ? "---" : tryLockVo.getId())
                            .keyName(name)
                            .keyValue(SchedulerTaskEnum.getValue(name))
                            .build();
                    String channelTag = SchedulerTaskUtils.getChannelTag();
                    if (!SchedulerTask.SUCCESS.equals(channelTag)){
                        assert channelTag != null;
                        if (channelTag.contains(SchedulerTaskUtils.getSplit())) {
                            String[] tag = channelTag.split(SchedulerTaskUtils.getSplit());
                            build.setStatus(tag[0]);
                            build.setMessage(tag[1]);
                        }else {
                            build.setStatus(channelTag);
                            build.setMessage("逻辑失败");
                        }

                    }else {
                        build.setStatus(channelTag);
                        build.setMessage("-");

                    }
                    historyService.saveHistory(build);
                }
                return proceed;
            } finally {
                if (lockStatus) {
                    //单点串行状态
                    // 释放锁
                    service.unlock(name);
                }
           }
        } else {
            // 未获取锁，跳过任务
            log.warn("==========【{}】:schedulerTaskLock 获取锁失败，跳过任务==================", name);
            return null;
        }
      }
}
