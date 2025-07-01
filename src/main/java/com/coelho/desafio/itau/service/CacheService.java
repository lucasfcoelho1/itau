package com.coelho.desafio.itau.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Optional;

@Service
public class CacheService {

    private final Jedis jedis;
    private final ObjectMapper objectMapper;
    private static final int DEFAULT_TTL_SECONDS = 3600;

    public CacheService(Jedis jedis) {
        this.jedis = jedis;
        this.objectMapper = new ObjectMapper();
    }

    public <T> void set(String key, T value) {
        set(key, value, DEFAULT_TTL_SECONDS);
    }

    public <T> void set(String key, T value, int ttlSeconds) {
        Optional.ofNullable(value)
                .map(this::toJson)
                .ifPresent(json -> jedis.setex(key, ttlSeconds, json));
    }

    public <T> T get(String key, Class<T> clazz) {
        return Optional.ofNullable(jedis.get(key))
                .flatMap(json -> fromJson(json, clazz))
                .orElse(null);
    }

    private <T> String toJson(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao serializar para cache: " + e.getMessage());
            return null;
        }
    }

    private <T> Optional<T> fromJson(String json, Class<T> clazz) {
        try {
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (Exception e) {
            System.err.println("Erro ao desserializar do cache: " + e.getMessage());
            return Optional.empty();
        }
    }
}
