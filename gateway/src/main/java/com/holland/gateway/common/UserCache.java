package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Validator;
import com.holland.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class UserCache extends Redis {
    private final Logger logger = LoggerFactory.getLogger(UserCache.class);

    /* MINUTES */
    @Value("${spring.redis.token-timeout:60}")
    private long tokenTimeout;
    @Value("${spring.redis.token-key-prefix:token_to_user:}")
    private String tokenKeyPrefix;
    @Value("${spring.redis.user-key-prefix:login_name_to_token:}")
    private String userKeyPrefix;

    public UserCache(@Value("${spring.redis.host}") String host
            , @Value("${spring.redis.port}") int port) {
        super(host, port);
    }

    public String cache(User user) {
        final String token = UUID.randomUUID().toString();
        final CacheUser cacheUser = CacheUser.from(user)
                .setToken(token)
                .setExpireTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tokenTimeout));

        final int seconds = (int) TimeUnit.MINUTES.toSeconds(tokenTimeout);
        exec(0, jedis -> jedis.setex(tokenKeyPrefix + token, seconds, JSON.toJSONString(cacheUser)));
        exec(1, jedis -> jedis.setex(userKeyPrefix + user.getLogin_name(), seconds, token));
        return token;
    }

    public CacheUser get(String token) {
        final String userStr = exec(0, jedis -> jedis.get(token));
        return JSON.parseObject(userStr, CacheUser.class);
    }

    public String getTokenByLoginName(String loginName) {
        return exec(1, jedis -> jedis.get(loginName));
    }

    public Boolean del(String token) {
        final CacheUser cacheUser = get(token);
        if (cacheUser != null) {
            exec(1, jedis -> jedis.del(userKeyPrefix + cacheUser.getLogin_name()));
        }
        exec(0, jedis -> jedis.del(tokenKeyPrefix + token));
        return true;
    }

    public Boolean delByLoginName(String loginName) {
        final String token = getTokenByLoginName(loginName);
        if (token != null) {
            exec(0, jedis -> jedis.del(tokenKeyPrefix + token));
        }
        exec(1, jedis -> jedis.del(userKeyPrefix + loginName));
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
