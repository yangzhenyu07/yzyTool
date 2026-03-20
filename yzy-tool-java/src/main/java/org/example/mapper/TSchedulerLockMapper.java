package org.example.mapper;

import org.example.entity.TSchedulerLock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 分布式锁定时器调度表 Mapper 接口
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-18 04:57:06
 */
@Mapper
public interface TSchedulerLockMapper extends BaseMapper<TSchedulerLock> {

}
