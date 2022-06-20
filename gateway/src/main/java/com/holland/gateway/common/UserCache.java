package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class UserCache extends RedisCache {
    /* MINUTES */
    @Value("${spring.redis.token-timeout:60}")
    private long tokenTimeout;
    @Value("${spring.redis.token-key-prefix:holland:}")
    private String tokenKeyPrefix;

    public String cache(String loginName, User user) {
        final String cut = cut(loginName);
        final String token = cut + UUID.randomUUID().toString().substring(8).replace("-", "");
        final CacheUser cacheUser = CacheUser.from(user)
                .setToken(token)
                .setExpireTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenTimeout));
        redisTemplate.opsForValue().set(tokenKeyPrefix + token, JSON.toJSONString(cacheUser), tokenTimeout, TimeUnit.MINUTES);
        return token;
    }

    public CacheUser get(String token) {
        return (CacheUser) redisTemplate.opsForValue().get(tokenKeyPrefix + token);
    }

    public CacheUser getByLoginName(String loginName) {
        for (String token : redisTemplate.keys(tokenKeyPrefix + cut(loginName) + "*")) {
            final CacheUser cacheUser = (CacheUser) redisTemplate.opsForValue().get(token);
            if (loginName.equals(cacheUser.getLoginName())) {
                return cacheUser;
            }
        }
        return null;
    }

    public Boolean del(String token) {
        return redisTemplate.delete(tokenKeyPrefix + token);
    }

    public Boolean delByLoginName(String loginName) {
        final CacheUser cacheUser = getByLoginName(loginName);
        return redisTemplate.delete(tokenKeyPrefix + cacheUser.getToken());
    }

    private String cut(String loginName) {
        if (loginName.length() < 8) {
            Validator.ParameterException.accept("loginName长度不能少于8");
        }
        return loginName.substring(0, 8);
    }

}
