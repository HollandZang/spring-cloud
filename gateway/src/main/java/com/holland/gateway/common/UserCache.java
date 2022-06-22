package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class UserCache extends RedisCache {
    private final Logger logger = LoggerFactory.getLogger(UserCache.class);

    /* MINUTES */
    @Value("${spring.redis.token-timeout:60}")
    private long tokenTimeout;
    @Value("${spring.redis.token-key-prefix:holland:}")
    private String tokenKeyPrefix;

    public String cache(String loginName, User user) {
        final String pre = getPre(loginName);
        final String token = pre + UUID.randomUUID().toString().substring(9).replace("-", "");
        final CacheUser cacheUser = CacheUser.from(user)
                .setToken(token)
                .setExpireTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenTimeout));
        redisTemplate.opsForValue().set(tokenKeyPrefix + token, JSON.toJSONString(cacheUser), tokenTimeout, TimeUnit.MINUTES);
        return token;
    }

    public CacheUser get(String token) {
        return JSON.parseObject((String) redisTemplate.opsForValue().get(token), CacheUser.class);
    }

    public CacheUser getByLoginName(String loginName) {
        for (String token : redisTemplate.keys(tokenKeyPrefix + getPre(loginName) + "*")) {
            final CacheUser cacheUser = JSON.parseObject((String) redisTemplate.opsForValue().get(token), CacheUser.class);
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
        if (cacheUser == null)
            return true;
        return redisTemplate.delete(tokenKeyPrefix + cacheUser.getToken());
    }

    private String cut(String loginName) {
        if (loginName.length() < 8) {
            Validator.ParameterException.accept("loginName长度不能少于8");
        }
        return loginName.substring(0, 8);
    }

    private String getPre(String loginName) {
        if (loginName.length() < 8) {
            Validator.ParameterException.accept("loginName长度不能少于8");
        }
        final char[] chars = loginName.toCharArray();
        char[] c = new char[9];
        System.arraycopy(chars, 0, c, 0, 3);
        System.arraycopy(chars, chars.length / 2 - 3, c, 3, 3);
        System.arraycopy(chars, chars.length - 3, c, 6, 3);
        return shuffle(c, new int[]{0, 7, 2, 8, 4, 5});
    }

    private String shuffle(char[] chars, int[] indexes) {
        for (int i = 0; i < indexes.length; i += 2) {
            chars[indexes[i]] += chars[indexes[i + 1]];
            chars[indexes[i + 1]] = (char) (chars[indexes[i]] - chars[indexes[i + 1]]);
            chars[indexes[i]] = (char) (chars[indexes[i]] - chars[indexes[i + 1]]);
        }
        return String.valueOf(chars);
    }

    private String change(String source, String target, int[] indexes) {
        if (source.length() != indexes.length) {
            logger.error("source.length must equals indexes.length");
            throw new RuntimeException();
        }
        final char[] chars = source.toCharArray();
        final char[] t = target.toCharArray();
        for (int i = 0; i < indexes.length; i++) {
            t[indexes[i]] = chars[i];
        }
        return String.valueOf(t);
    }
}
