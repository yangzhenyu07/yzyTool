package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.annotation.constant.SchedulerTaskEnum;
import org.example.annotation.vo.TryLockVo;
import org.example.config.LogTracer;
import org.example.entity.TSchedulerLock;
import org.example.mapper.TSchedulerLockMapper;
import org.example.service.TSchedulerLockService;
import org.example.util.UuidUtil;
import org.example.vo.LogTracerVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 分布式锁定时器调度表 服务实现类
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-17 02:57:23
 */
@Slf4j
@Service
public class TSchedulerLockServiceImpl extends ServiceImpl<TSchedulerLockMapper, TSchedulerLock> implements TSchedulerLockService {
    @Resource
    private TSchedulerLockMapper mapper;
    @Resource
    private LogTracer logTracer;

    /**
     * 获取当前锁的过期时间
     *
     * @param keyName 锁的名称
     * @return 锁的过期时间
     */
    private Optional<TSchedulerLock> getTSchedulerLock(String keyName) {
        try {
            QueryWrapper<TSchedulerLock> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("KEY_NAME", keyName)
                    .orderByDesc("CREATE_TIME"); // 假设按 CREATE_TIME 降序排序

            List<TSchedulerLock> list = mapper.selectList(queryWrapper);
            if (list != null && !list.isEmpty()) {
                return Optional.of(list.get(0)); // 取排序后的第一条
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    /**
     * 尝试获取锁
     *
     * @param keyName       锁的名称
     * @param lockAtMostFor     锁的缓存时间
     * @param lockMostTime     锁的过期时间
     * @param lockStatus     锁状态
     * @return 是否成功获取锁
     */
    @Transactional
    @Override
    public TryLockVo tryLock(String keyName, long lockAtMostFor, long lockMostTime,boolean lockStatus) {
        Date now = new Date();
        long currentTimestamp = System.currentTimeMillis();
        long futureTimestamp = currentTimestamp + lockAtMostFor;
        Date lockUntilDate = new Date(futureTimestamp);
        // 获取锁的当前状态
        Optional<TSchedulerLock> lockOptional = getTSchedulerLock(keyName);

        // 1. 锁不存在：尝试插入新锁
        if (!lockOptional.isPresent()) {
            try {
                String id = UuidUtil.simpleUUID();
                TSchedulerLock newLock = TSchedulerLock.builder()
                        .pkTSchedulerLock(id)
                        .lockUntil(lockUntilDate)
                        .keyName(keyName)
                        .keyValue(SchedulerTaskEnum.getValue(keyName))
                        .lockedBy(logTracer.getTrace().getIp())
                        .traceId(logTracer.getTrace().getTraceId())
                        .lockState(1)
                        .lockVersion(0)
                        .createTime(now)
                        .build();
                if (!lockStatus){
                    //无锁状态
                    newLock.setLockState(0);
                }
                mapper.insert(newLock);
                return TryLockVo.builder().status(Boolean.TRUE).id(id).build();

            } catch (Exception e) {
                log.info("尝试获取锁失败，可能是并发导致的插入失败");
                return TryLockVo.builder().status(Boolean.FALSE).build();
            }
        }

        TSchedulerLock currentLock = lockOptional.get();
        String pkTSchedulerLock = currentLock.getPkTSchedulerLock();
        log.info("尝试获取锁【{}】, 当前锁状态：{}", keyName, currentLock.getLockState());

        if (!lockStatus){
            //无锁
            return TryLockVo.builder().status(Boolean.TRUE).id(pkTSchedulerLock).build();
        }

        // 2. 锁存在且未过期：直接返回失败

        if (currentLock.getLockUntil().after(now)) {
            log.info("==========【{}】: 锁未过期，当前节点无法获取锁", keyName);
            return TryLockVo.builder().status(Boolean.FALSE).build();
        }

        // 3. 锁存在且过期：尝试更新锁
        Integer lockVersion = currentLock.getLockVersion();

        // 判断是否需要解锁
        boolean unlock = Boolean.FALSE;
        Date lockUntil = currentLock.getLockUntil();
        long lockTime = lockUntil.getTime();
        long timeDifference = currentTimestamp - lockTime;
        if (timeDifference > lockMostTime && currentLock.getLockState() == 1) {
            log.info("==========【{}】:========锁已过期，可以解锁=========", keyName);
            unlock = Boolean.TRUE; // 锁已过期，可以解锁
        }

        // 通过乐观锁机制更新锁
        TSchedulerLock updatedLock = TSchedulerLock.builder()
                .lockUntil(lockUntilDate)
                .traceId(logTracer.getTrace().getTraceId())
                .lockVersion(lockVersion + 1)
                .keyValue(SchedulerTaskEnum.getValue(keyName))
                .lockedBy(logTracer.getTrace().getIp())
                .lockState(1)  // 锁定状态为 1
                .build();

        UpdateWrapper<TSchedulerLock> updateWrapper = new UpdateWrapper<>();
        if (!unlock) {
            updateWrapper.eq("KEY_NAME", keyName).eq("LOCK_STATE", 0).eq("LOCK_VERSION", lockVersion);
        } else {
            //解锁逻辑
            updatedLock.setLockState(0);
            updateWrapper.eq("KEY_NAME", keyName).eq("LOCK_STATE", 1).eq("LOCK_VERSION", lockVersion);
        }

        // 尝试更新锁
        int updatedRows = mapper.update(updatedLock, updateWrapper);
        if (updatedRows == 0) {
            log.info("==========【{}】: 并发冲突，无法获取锁", keyName);
            return TryLockVo.builder().status(Boolean.FALSE).build();
        }

        return TryLockVo.builder().status(Boolean.TRUE).id(pkTSchedulerLock).build();
    }

    /**
     * 释放锁
     *
     * @param keyName 锁的名称
     */
    @Transactional
    @Override
    public void unlock(String keyName) {
        LogTracerVo trace = logTracer.getTrace();
        String lockedBy = trace.getIp();
        Date newDate = new Date();
        Date delayedUnlockTime = new Date(newDate.getTime() + 1000 * 60);

        TSchedulerLock tSchedulerLock = TSchedulerLock.builder()
                .lockUntil(delayedUnlockTime)
                .updateTime(newDate)
                .lockState(0)
                .lockedBy(lockedBy).build();

        UpdateWrapper<TSchedulerLock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("KEY_NAME", keyName);
        mapper.update(tSchedulerLock,updateWrapper);

    }



}
