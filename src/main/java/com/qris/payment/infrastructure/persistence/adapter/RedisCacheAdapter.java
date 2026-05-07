package com.qris.payment.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qris.payment.application.port.out.CachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheAdapter implements CachePort {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheAdapter.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheAdapter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void put(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
            log.debug("Cached key: {} with TTL: {}ms", key, ttl.toMillis());
        } catch (Exception e) {
            log.warn("Failed to cache key: {}. Error: {}", key, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            T result = objectMapper.convertValue(value, type);
            log.debug("Cache hit for key: {}", key);
            return Optional.of(result);
        } catch (Exception e) {
            log.warn("Failed to get cached key: {}. Error: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Evicted cache key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to evict key: {}. Error: {}", key, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Failed to check key existence: {}. Error: {}", key, e.getMessage());
            return false;
        }
    }
}
