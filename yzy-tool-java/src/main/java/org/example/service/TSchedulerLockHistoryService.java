package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.annotation.vo.LockHistoryVo;
import org.example.entity.TSchedulerLockHistory;
import org.example.vo.TSchedulerLockHistoryVo;

/**
 * <p>
 * 分布式锁定时器调度历史表 服务类
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-19 11:07:39
 */
public interface TSchedulerLockHistoryService extends IService<TSchedulerLockHistory> {
    void saveHistory(LockHistoryVo history);

    Page<TSchedulerLockHistoryVo> queryHistory(String name,int pageNo,  int pageSize);

    Page<TSchedulerLockHistoryVo> queryHistoryByFkId(String fkId,int pageNo,  int pageSize);
}
