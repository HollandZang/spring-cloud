package com.holland.gateway.common;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

@Configuration
public abstract class RedisCache {
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        if (factory instanceof LettuceConnectionFactory) {
            final LettuceConnectionFactory f = ((LettuceConnectionFactory) factory);
            f.setDatabase(0);
        }
        if (factory instanceof JedisConnectionFactory) {
            final JedisConnectionFactory f = ((JedisConnectionFactory) factory);
            final RedisSentinelConfiguration sentinelConfiguration = f.getSentinelConfiguration();
            if (sentinelConfiguration != null) {
                sentinelConfiguration.setDatabase(0);
            } else {
                f.setDatabase(0);
            }
        }
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.afterPropertiesSet();
        return template;
    }
}
