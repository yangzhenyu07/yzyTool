package org.example.config.redis;


import org.example.config.redis.constants.CacheConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 订阅配置
 */
@Configuration
public class RedisMessageListenerConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("redisStdTemplate")
            RedisTemplate<String, Object> redisTemplate,
            CacheMessageListener listener) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        if (redisTemplate.getConnectionFactory() != null) {
            container.setConnectionFactory(redisTemplate.getConnectionFactory());
        }
        container.addMessageListener(
                listener,
                new ChannelTopic(CacheConstants.CACHE_INVALIDATE_TOPIC)
        );

        return container;
    }
}