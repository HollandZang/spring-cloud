package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.holland.common.entity.gateway.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}




