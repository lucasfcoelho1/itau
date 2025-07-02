package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.config.RedisConfig;
import com.coelho.desafio.itau.diplomat.wire.out.CountryDogWireOut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, CountryDogWireOut> redisTemplate;

    public CacheService(RedisTemplate<String, CountryDogWireOut> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, CountryDogWireOut value) {
        redisTemplate.opsForValue().set(key, value, RedisConfig.TTL_SECONDS, TimeUnit.SECONDS);
    }

    public Optional<CountryDogWireOut> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}