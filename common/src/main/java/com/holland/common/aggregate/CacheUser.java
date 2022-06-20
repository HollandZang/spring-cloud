package com.holland.common.aggregate;

import com.holland.common.entity.gateway.User;

public class CacheUser extends User {
    private Long expireTime;
    private String token;

    public static CacheUser from(User user) {
        return (CacheUser) new CacheUser()
                .setId(user.getId())
                .setLoginName(user.getLoginName())
                .setCreateTime(user.getCreateTime())
                .setUpdateTime(user.getUpdateTime());
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
