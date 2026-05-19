package org.example.config.redis.constants;

public interface CacheConstants {

    String USER_KEY_PREFIX = "user:";

    String CACHE_INVALIDATE_TOPIC = "cache:invalidate";

    long REDIS_TTL_SECONDS = 30 * 60; // 30分钟
}