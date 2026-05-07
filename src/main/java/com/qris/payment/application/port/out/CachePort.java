package com.qris.payment.application.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Output port for cache operations (Redis).
 */
public interface CachePort {

    void put(String key, Object value, Duration ttl);

    <T> Optional<T> get(String key, Class<T> type);

    void evict(String key);

    boolean exists(String key);
}
