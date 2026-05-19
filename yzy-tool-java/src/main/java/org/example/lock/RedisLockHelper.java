package org.example.lock;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 锁辅助工具类
 * @author 杨镇宇
 * @date 2025/2/17 10:43
 * @version 1.1
 */
@Slf4j
@Component
public class RedisLockHelper {


    private static String lockScript;
    private static String unlockScript;


    static {
        ByteBuffer buff;
        try (InputStream stream = Objects.requireNonNull(RedisLockHelper.class.getResource("/lock.lua")).openStream(); ReadableByteChannel channel = Channels.newChannel(stream)) {
            buff = ByteBuffer.allocate(800);
            channel.read(buff);
            lockScript = new String(buff.array(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            log.error( "Load lock.lua error.", e);
        }

        try (InputStream stream = Objects.requireNonNull(RedisLockHelper.class.getResource("/unlock.lua")).openStream(); ReadableByteChannel channel = Channels.newChannel(stream)) {
            buff = ByteBuffer.allocate(1024);
            channel.read(buff);
            unlockScript = new String(buff.array(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            log.error( "Load unlock.lua error.", e);
        }
    }

    @Qualifier("redisStdTemplate")
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;



    /**
     * 尝试获取分布式锁
     * @param key 锁的 key
     * @param value 锁的值
     * @param expire 锁的过期时间（秒）
     * @return 是否成功获取到锁
     */
    boolean tryLock(String key, String value, long expire) {
        log.info("Trying to acquire lock: key=" + key + ", value=" + value + ", expire=" + expire);

        Long execute = redisTemplate.execute(
                new DefaultRedisScript<>(lockScript, Long.class),
                Collections.singletonList(key),
                value, // 锁的值
                expire// 锁的过期时间（秒）
        );
        if (ObjectUtil.isNull(execute)) {
            log.error("Redis script execution failed.");
        } else if (execute.equals(0L)) {
            log.error("Failed to acquire lock: Expire time is invalid.");
        }else if (execute.equals(2L)) {
            log.info("lock: key already exists.");
        } else {
            log.info("Lock acquired execute:{}",execute);
        }
        return null != execute && execute.equals(1L);  // 如果返回 1，表示获取锁成功
    }


    /**
     * 非 lua脚本的方式
     * @param key
     * @param value
     * @param expire
     * @return
     */
    boolean tryLock_no_lua(String key, String value, long expire) {
        // 检查过期时间是否有效
        if (expire <= 0) {
            log.error("Failed to acquire lock: Expire time is invalid.");
            return false;
        }

        // 使用 setnx 尝试获取锁，setnx 仅当 key 不存在时才会成功
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(expire));

        if (lockAcquired == null || !lockAcquired) {
            log.info("lock: key already exists.");
            return false; // 锁已经存在
        }

        // 如果 setnx 成功，则设置过期时间（setIfAbsent 已经设置了过期时间）
        //即使 setIfAbsent 已经设置了过期时间，我们也再次调用 expire 来明确设置过期时间。这是为了确保锁的过期时间正确。
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);

        log.info("Lock acquired: key={} value={} expire={}", key, value, expire);
        return true;  // 获取锁成功
    }

    /**
     * 非 LUA 脚本
     * @param key
     * @param value
     * @return
     */
    boolean releaseLock_no_lua(String key, String value) {
        // 获取锁的当前值
        String currentValue = (String) redisTemplate.opsForValue().get(key);

        // 如果锁的值与当前值匹配，删除锁
        if (value.equals(currentValue)) {
            // 删除锁
            redisTemplate.delete(key);
            log.info("Lock released: key={} value={}", key, value);
            return true;  // 释放锁成功
        }
        return false; // 锁值不匹配，释放失败

    }
    /**
     * 释放分布式锁
     * @param key 锁的 key
     * @param value 锁的值
     * @return 是否成功释放锁
     */
    boolean releaseLock(String key, String value) {
        Long execute = redisTemplate.execute(
                new DefaultRedisScript<>(unlockScript, Long.class),
                Collections.singletonList(key),
                value

        );
        return null != execute && execute.equals(1L);  // 如果返回 1，表示成功释放锁
    }


}
