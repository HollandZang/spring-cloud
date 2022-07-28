package com.holland.common.aggregate;

import com.holland.common.entity.gateway.User;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CacheUser extends User {
    private Long expireTime;
    private String token;

    public static CacheUser from(User user) {
        final CacheUser cacheUser = new CacheUser();
        BeanUtils.copyProperties(user, cacheUser);
        return cacheUser;
    }

    public CacheUser refresh(User user) {
        for (Field field : user.getClass().getDeclaredFields()) {
            final int i = field.getModifiers();
            if (Modifier.isPrivate(i) && !Modifier.isFinal(i) && !Modifier.isStatic(i)) {
                field.setAccessible(true);
                try {
                    final Object o = field.get(user);
                    field.set(this, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.setRoles(user.getRoles());
        return this;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public CacheUser setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public String getToken() {
        return token;
    }

    public CacheUser setToken(String token) {
        this.token = token;
        return this;
    }
}
