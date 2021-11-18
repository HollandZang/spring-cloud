package com.holland.gateway.mapper;

import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.common.entity.gateway.LogLogin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.common.entity.gateway.LogLogin
 */
@Mapper
public interface LogLoginMapper {

    int insertSelective(LogLogin record);

    LogLogin selectByPrimaryKey(Long id);

    List<LogLogin> list(PageHelper pageHelper);

    Long count();
}




