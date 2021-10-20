package com.holland.gateway.mapper;

import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * @Entity com.holland.gateway.domain.User
 */
@Mapper
public interface UserMapper {

    int insertSelective(User record);

    Optional<User> selectByLoginName(String loginName);

    List<User> list(PageHelper pageHelper);

    int updateByUserSelective(User record);

    Long count();
}




