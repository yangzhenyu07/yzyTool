

package org.example.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.example.annotation.vo.LockHistoryVo;
import org.example.config.LogTracer;
import org.example.dto.TSchedulerLockHistoryDTO;
import org.example.entity.TSchedulerLockHistory;
import org.example.mapper.TSchedulerLockHistoryMapper;
import org.example.service.TSchedulerLockHistoryService;
import org.example.util.UuidUtil;
import org.example.vo.LogTracerVo;
import org.example.vo.TSchedulerLockHistoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <p>
 * 分布式锁定时器调度历史表 服务实现类
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-19 11:07:39
 */
@Slf4j
@Service
public class TSchedulerLockHistoryServiceImpl extends ServiceImpl<TSchedulerLockHistoryMapper, TSchedulerLockHistory> implements TSchedulerLockHistoryService {
    @Resource
    private LogTracer logTracer;
    @Resource
    private TSchedulerLockHistoryMapper mapper;
    /**
     * 复制相同的属性到新对象里面
     *
     * @param source 有数据的对象
     * @param target 新对象
     * @param <T>    泛型
     * @return 新对象
     */
    public static <T> T copy(Object source, T target) throws NoSuchFieldException, IllegalAccessException {
        if (Map.class.isAssignableFrom(source.getClass())) {
            Class clazz = target.getClass();
            Map<String,? extends Object> map = (Map<String, ? extends Object>) source;
            Set keys = map.keySet();
            for (Object key:keys){
                Field field = clazz.getDeclaredField(key.toString());
                Object value = map.get(key);
                field.setAccessible(true);
                field.set(target,value);
            }
        } else {
            BeanUtils.copyProperties(source,target);
        }
        return target;
    }
    /**
     * 复制相同的属性到新对象里面
     *
     * @param sourceList 有数据的对象集合
     * @param clz        新对象的类型
     * @param <T>        泛型
     * @return 新对象
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> clz) {
        List<T> finalResult = new ArrayList<>(sourceList.size());

        sourceList.forEach(v -> {
            try {
                T obj = clz.getDeclaredConstructor().newInstance();
                copy(v, obj);
                finalResult.add(obj);
            } catch (Exception e) {
                log.error("复制相同的属性到新对象里面失败",e);
            }
        });
        return finalResult;
    }
    @Transactional
    @Override
    public void saveHistory(LockHistoryVo history) {

        LogTracerVo trace = logTracer.getTrace();
        String ip = trace.getIp();
        String traceId = trace.getTraceId();
        TSchedulerLockHistory build = TSchedulerLockHistory.builder()
                .fkTSchedulerLock(history.getId())
                .keyName(history.getKeyName())
                .keyValue(history.getKeyValue())
                .resultState(history.getStatus())
                .resultBy(ip)
                .traceId(traceId)
                .pkTSchedulerLockHistory(UuidUtil.simpleUUID())
                .createTime(new Date()).build();
        if (ObjectUtil.isNotNull(history.getMessage())){
            build.setErrorMessage(history.getMessage());
        }
        int insert = mapper.insert(build);
        if (0 == insert){
            log.error("【"+history.getKeyName()+"】schedulerTaskLock ================新增定时器调度历史信息失败");
        }

    }

    @Override
    public Page<TSchedulerLockHistoryVo> queryHistory(String name,int pageNo,  int pageSize) {

        // 创建 Page 对象，设置当前页和每页条数
        Page<TSchedulerLockHistoryDTO> pageDto = new Page<>(pageNo, pageSize);
        // 返回分页结果
        Page<TSchedulerLockHistoryVo> resultPage = new Page<>(pageNo, pageSize);
        // 调用 Mapper 查询分页数据
        Page<TSchedulerLockHistoryDTO> tSchedulerLockHistoryDTOPage = mapper.queryHistory(pageDto, name);
        List<TSchedulerLockHistoryDTO> records = tSchedulerLockHistoryDTOPage.getRecords();
        if (CollectionUtils.isEmpty(records)){
            return resultPage;
        }
        List<TSchedulerLockHistoryVo> tSchedulerLockHistoryVos = copyList(records, TSchedulerLockHistoryVo.class);
        resultPage.setRecords(tSchedulerLockHistoryVos);
        resultPage.setTotal(tSchedulerLockHistoryDTOPage.getTotal());

        return resultPage;
    }

    @Override
    public Page<TSchedulerLockHistoryVo> queryHistoryByFkId(String fkId, int pageNo, int pageSize) {
        // 创建 Page 对象，设置当前页和每页条数
        Page<TSchedulerLockHistory> page = new Page<>(pageNo, pageSize);
        // 返回分页结果
        Page<TSchedulerLockHistoryVo> resultPage = new Page<>(pageNo, pageSize);
        UpdateWrapper<TSchedulerLockHistory> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("FK_T_SCHEDULER_LOCK", fkId);
        // 添加倒序排序，假设按 create_time 字段倒序排序
        queryWrapper.orderByDesc("CREATE_TIME");
        Page<TSchedulerLockHistory> lockHistoryPage = mapper.selectPage(page, queryWrapper);
        List<TSchedulerLockHistory> records = lockHistoryPage.getRecords();
        if (CollectionUtils.isEmpty(records)){
            return resultPage;
        }
        List<TSchedulerLockHistoryVo> tSchedulerLockHistoryVos = copyList(records, TSchedulerLockHistoryVo.class);
        resultPage.setRecords(tSchedulerLockHistoryVos);
        resultPage.setTotal(lockHistoryPage.getTotal());
        return resultPage;
    }
}
