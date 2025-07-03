package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.config.RedisConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> void set(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(RedisConfig.TTL_SECONDS));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String key, Class<T> objectClass) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, objectClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}