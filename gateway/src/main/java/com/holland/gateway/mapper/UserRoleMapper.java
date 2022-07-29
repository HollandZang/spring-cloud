package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    default UserRole getByLogin_name(String loginName) {
        return this.selectOne(new QueryWrapper<UserRole>().eq("login_name", loginName));
    }
}
