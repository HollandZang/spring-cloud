package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
public class RedisController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.redis.token-timeout:60}")
    private long tokenTimeout;
    @Value("${spring.redis.token-key-prefix:holland}")
    private String tokenKeyPrefix;

    public String setToken(String loginName, Object user) {
        final String token = loginName + '|' + DateUtil.getDateStr();
        redisTemplate.opsForValue().set(tokenKeyPrefix + token, JSON.toJSONString(user), tokenTimeout, TimeUnit.MINUTES);
        return token;
    }

    public Object getToken(String token) {
        return redisTemplate.opsForValue().get(tokenKeyPrefix + token);
    }


    public Boolean delToken(String token) {
        return redisTemplate.delete(tokenKeyPrefix + token);
    }

    public void delToken(User user) {
        redisTemplate.keys(tokenKeyPrefix + user.getLoginName() + "|*")
                .forEach(redisTemplate::delete);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
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
