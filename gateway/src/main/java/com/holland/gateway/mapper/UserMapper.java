package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.holland.common.entity.gateway.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    default User getByLogin_name(String loginName) {
        return this.selectOne(new QueryWrapper<User>().eq("login_name", loginName));
    }
}




