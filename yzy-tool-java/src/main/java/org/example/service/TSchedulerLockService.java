package org.example.service;

import org.example.annotation.vo.TryLockVo;
import org.example.entity.TSchedulerLock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 分布式锁定时器调度表 服务类
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-17 02:57:23
 */
public interface TSchedulerLockService extends IService<TSchedulerLock> {
    /**
     * 尝试获取锁
     *
     * @param keyName       锁的名称
     * @param lockAtMostFor     锁的缓存时间
     * @param lockMostTime     锁的过期时间
     * @param lockStatus     锁状态
     * @return 是否成功获取锁
     */
    TryLockVo tryLock(String keyName, long lockAtMostFor, long lockMostTime,boolean lockStatus);
    /**
     * 释放锁
     *
     * @param keyName 锁的名称
     */
    void unlock(String keyName);



}
