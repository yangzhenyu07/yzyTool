package org.example.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.TSchedulerLockHistoryDTO;
import org.example.entity.TSchedulerLockHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 分布式锁定时器调度历史表 Mapper 接口
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-19 02:49:13
 */
@Mapper
public interface TSchedulerLockHistoryMapper extends BaseMapper<TSchedulerLockHistory> {
    Page<TSchedulerLockHistoryDTO> queryHistory(Page<TSchedulerLockHistoryDTO> page, String name);

}
