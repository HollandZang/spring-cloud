package com.holland.gateway.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.entity.gateway.UserRole;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.gateway.mapper.UserMapper;
import com.holland.gateway.mapper.UserRoleMapper;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserCache userCache;

    @Transactional
    public void updateUser(ServerHttpRequest request, User user, CacheUser cacheUser, String login_name, BCryptPasswordEncoder encoder) {
        if (StringUtils.hasText(user.getPassword()))
            user.setPassword(encoder.encode(user.getPassword()));

        userMapper.updateById(user
                .setId(cacheUser.getId())
                .setUpdate_time(new Date()));

        if (user.getRoles() != null && !cacheUser.getRoles().equals(user.getRoles())) {
            userRoleMapper.update(new UserRole().setRoles(user.getRoles())
                    , new UpdateWrapper<UserRole>().eq("login_name", login_name));
        }

        userCache.refresh(RequestUtil.getToken(request), user);
    }
}
