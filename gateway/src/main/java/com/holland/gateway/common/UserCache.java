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
    @Value("${spring.redis.token-key-prefix:token_to_user:}")
    private String tokenKeyPrefix;
    @Value("${spring.redis.user-key-prefix:login_name_to_token:}")
    private String userKeyPrefix;

    public String cache(User user) {
        final String token = UUID.randomUUID().toString();
        final CacheUser cacheUser = CacheUser.from(user)
                .setToken(token)
                .setExpireTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenTimeout));
        redisTemplate.opsForValue().set(tokenKeyPrefix + token, JSON.toJSONString(cacheUser), tokenTimeout, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(userKeyPrefix + user.getLogin_name(), token, tokenTimeout, TimeUnit.MINUTES);
        return token;
    }

    public CacheUser get(String token) {
        return JSON.parseObject((String) redisTemplate.opsForValue().get(token), CacheUser.class);
    }

    public String getTokenByLoginName(String loginName) {
        return (String) redisTemplate.opsForValue().get(userKeyPrefix + loginName);
    }

    public Boolean del(String token) {
        final CacheUser cacheUser = get(token);
        if (cacheUser != null) {
            redisTemplate.delete(userKeyPrefix + cacheUser.getLogin_name());
        }
        redisTemplate.delete(tokenKeyPrefix + token);
        return true;
    }

    public Boolean delByLoginName(String loginName) {
        final String token = getTokenByLoginName(loginName);
        if (token != null) {
            redisTemplate.delete(tokenKeyPrefix + token);
        }
        redisTemplate.delete(userKeyPrefix + loginName);
        return true;
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
