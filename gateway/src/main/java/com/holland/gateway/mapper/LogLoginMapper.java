package com.holland.gateway.mapper;

import com.holland.gateway.domain.LogLogin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.gateway.domain.LogLogin
 */
@Mapper
public interface LogLoginMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LogLogin record);

    int insertSelective(LogLogin record);

    LogLogin selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LogLogin record);

    int updateByPrimaryKey(LogLogin record);

    List<LogLogin> list(int offset, Integer limit);

    Long count();
}




