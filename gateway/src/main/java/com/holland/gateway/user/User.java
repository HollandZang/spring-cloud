package com.holland.gateway.user;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String loginName;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public User setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public User setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
