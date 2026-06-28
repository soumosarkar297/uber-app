package com.rideshare.locationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis connection and template setup. Configures
 * RedisTemplate with String serializers for human-readable data in Redis CLI.
 * Uses Spring Data Redis to provide geo-spatial operations for driver location
 * tracking.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    /**
     * Creates and configures a RedisTemplate for String key-value operations.
     * Uses StringRedisSerializer for keys, values, hash keys, and hash values
     * to ensure data is human-readable when inspected via Redis CLI.
     *
     * @param connectionFactory the Redis connection factory
     * @return configured RedisTemplate instance
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String serializers make data human-readable in Redis CLI
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}
