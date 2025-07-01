package com.coelho.desafio.itau.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import redis.clients.jedis.Jedis;

import java.time.Duration;

@Configuration
public class RedisConfig {

    public static final int TTL_SECONDS = 3600;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues();
    }

    @Bean
    public Jedis jedis() {
        return new Jedis("localhost", 6379);
    }

}