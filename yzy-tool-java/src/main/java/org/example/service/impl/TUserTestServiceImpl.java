package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.example.config.redis.CacheMessage;
import org.example.config.redis.constants.CacheConstants;
import org.example.entity.TUserTest;
import org.example.mapper.TUserTestMapper;
import org.example.service.TUserTestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo.TUserTestVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

/**
 * <p>
 * 测试表 服务实现类
 * </p>
 *
 * @author yangzhenyu
 * @since 2026-05-14 11:52:16
 */
@Slf4j
@Service
public class TUserTestServiceImpl extends ServiceImpl<TUserTestMapper, TUserTest> implements TUserTestService {
    @Resource(name = "localCache")
    private Cache<String, Object> localCache;
    @Resource(name = "redisStdTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private TUserTestMapper mapper;

    public TUserTest getById(String code) {
        String key = CacheConstants.USER_KEY_PREFIX + code;
        TUserTest localUser = (TUserTest) localCache.getIfPresent(key);
        if (localUser != null) {
            log.info("L1 Caffeine hit: {}", key);
            return localUser;
        }

        // 2. 查询 Redis
        TUserTest redisUser = (TUserTest) redisTemplate.opsForValue().get(key);
        if (redisUser != null) {
            log.info("L2 Redis hit: {}", key);
            localCache.put(key, redisUser);
            return redisUser;
        }

        // 3. 查询 DB
        log.info("DB hit: {}", key);
        QueryWrapper<TUserTest> wrapper = new QueryWrapper<>();
        wrapper.eq("CODE", code);
        TUserTest tUserTest = mapper.selectOne(wrapper);

        if (null != tUserTest) {
            // 写 Redis
            redisTemplate.opsForValue().set(
                    key,
                    tUserTest,
                    CacheConstants.REDIS_TTL_SECONDS,
                    TimeUnit.SECONDS
            );

            // 写本地缓存
            localCache.put(key, tUserTest);
        }

        return tUserTest;
    }

    @Transactional
    public int update(TUserTestVo user) {
        String key = CacheConstants.USER_KEY_PREFIX + user.getCode();
        UpdateWrapper<TUserTest> updateWrapper = new UpdateWrapper<>();
        TUserTest updated = new TUserTest();
        BeanUtils.copyProperties(user,updated);
        updateWrapper.eq("CODE", user.getCode());
        updated.setCode(null);
        // 1. 更新 DB
        int update = mapper.update(updated, updateWrapper);

        // 2. 删除 Redis
        redisTemplate.delete(key);

        // 3. 发布消息
        redisTemplate.convertAndSend(
                CacheConstants.CACHE_INVALIDATE_TOPIC,
                new CacheMessage(key)
        );

        // 4. 当前节点也删除本地缓存（降低消息延迟窗口）
        localCache.invalidate(key);

        log.info("更新完成，缓存已失效 key={}", key);
        return update;
    }



    @Transactional
    public int save(TUserTestVo user) {
        String key = CacheConstants.USER_KEY_PREFIX + user.getCode();

        // 1. 插入数据库
        TUserTest target = new TUserTest();
        BeanUtils.copyProperties(user,target);
        int insert = mapper.insert(target);

        // 2. 写 Redis
        redisTemplate.opsForValue().set(
                key,
                target,
                CacheConstants.REDIS_TTL_SECONDS,
                TimeUnit.SECONDS
        );

        // 3. 写本地缓存
        localCache.put(key, target);

        log.info("新增成功，写入缓存 key={}", key);
        return insert;
    }

    @Transactional
    public int delete(String code) {
        String key = CacheConstants.USER_KEY_PREFIX + code;

        // 1. 删除数据库
        QueryWrapper<TUserTest> wrapper = new QueryWrapper<>();
        wrapper.eq("CODE", code);
        int delete = mapper.delete(wrapper);

        // 2. 删除 Redis
        redisTemplate.delete(key);

        // 3. 广播通知
        redisTemplate.convertAndSend(
                CacheConstants.CACHE_INVALIDATE_TOPIC,
                new CacheMessage(key)
        );

        // 4. 删除当前节点 L1
        localCache.invalidate(key);

        log.info("删除成功，缓存已清理 key={}", key);
        return delete;
    }
}
