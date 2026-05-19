package org.example.config.redis;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息监听器
 */
@Slf4j
@Component
public class CacheMessageListener implements MessageListener {
    @Resource(name = "localCache")
    private  Cache<String, Object> localCache;
    @Resource
    private  Jackson2JsonRedisSerializer<Object> serializer;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object obj = serializer.deserialize(message.getBody());

        if (!(obj instanceof CacheMessage)) {
            return;
        }

        CacheMessage cacheMessage = (CacheMessage) obj;

        localCache.invalidate(cacheMessage.getCacheKey());

        log.info("收到缓存失效通知，删除本地缓存 key={}",
                cacheMessage.getCacheKey());
    }
}
