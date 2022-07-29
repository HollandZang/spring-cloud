package com.holland.common.entity.gateway;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.holland.common.enums.gateway.RoleEnum;

import java.util.Date;

public class UserRole {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String login_name;

    private String roles;

    private Date update_time;

    public RoleEnum[] parseRoles() {
        final String[] split = roles.split(",");
        final RoleEnum[] enums = new RoleEnum[split.length];
        for (int i = 0; i < split.length; i++) {
            enums[i] = RoleEnum.find(split[i]);
        }
        return enums;
    }

    public Integer getId() {
        return id;
    }

    public UserRole setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getLogin_name() {
        return login_name;
    }

    public UserRole setLogin_name(String login_name) {
        this.login_name = login_name;
        return this;
    }

    public String getRoles() {
        return roles;
    }

    public UserRole setRoles(String roles) {
        this.roles = roles;
        return this;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public UserRole setUpdate_time(Date update_time) {
        this.update_time = update_time;
        return this;
    }
}
