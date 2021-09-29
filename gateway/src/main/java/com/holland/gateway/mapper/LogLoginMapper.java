package com.holland.gateway.mapper;

import com.holland.gateway.sqlHelper.PageHelper;
import com.holland.gateway.domain.LogLogin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.gateway.domain.LogLogin
 */
@Mapper
public interface LogLoginMapper {

    int insertSelective(LogLogin record);

    LogLogin selectByPrimaryKey(Long id);

    List<LogLogin> list(PageHelper pageHelper);

    Long count();
}




