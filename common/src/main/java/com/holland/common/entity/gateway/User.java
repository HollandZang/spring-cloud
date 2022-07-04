package com.holland.common.entity.gateway;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 用户信息表
 *
 * @TableName user
 */
public class User implements UserDetails {
    @TableField(exist = false)
    private final LocalDateTime loginTime = LocalDateTime.now();

    /**
     *
     */
    @TableId
    private Integer id;

    /**
     *
     */
    private String login_name;

    /**
     *
     */
    private String password;

    /**
     *
     */
    private Date create_time;

    /**
     *
     */
    private Date update_time;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getLogin_name() {
        return login_name;
    }

    public User setLogin_name(String login_name) {
        this.login_name = login_name;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("admin"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login_name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return loginTime.plusMinutes(30).isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public User setCreate_time(Date create_time) {
        this.create_time = create_time;
        return this;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public User setUpdate_time(Date update_time) {
        this.update_time = update_time;
        return this;
    }

}