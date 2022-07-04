package com.holland.common.aggregate;

import com.holland.common.entity.gateway.User;

public class CacheUser extends User {
    private Long expireTime;
    private String token;

    public static CacheUser from(User user) {
        return (CacheUser) new CacheUser()
                .setId(user.getId())
                .setLogin_name(user.getLogin_name())
                .setCreate_time(user.getCreate_time())
                .setUpdate_time(user.getUpdate_time());
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
