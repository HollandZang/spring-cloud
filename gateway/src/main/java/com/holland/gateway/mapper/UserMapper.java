package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.sqlHelper.PageHelper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * @Entity com.holland.common.entity.gateway.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    Optional<User> selectByLoginName(String loginName);

    List<User> list(PageHelper pageHelper);

    int updateByUserSelective(User record);

    Long count();
}




