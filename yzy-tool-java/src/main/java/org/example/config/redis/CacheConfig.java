package org.example.config.redis;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)         // 最多存 1 万条数据
                .expireAfterWrite(5, TimeUnit.MINUTES) // 写入5分钟后过期
                .build();                    // 创建缓存实例
    }
}