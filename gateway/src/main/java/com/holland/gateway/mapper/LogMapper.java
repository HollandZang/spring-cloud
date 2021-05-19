package com.holland.gateway.mapper;

import com.holland.gateway.domain.Log;

import java.util.List;

/**
 * @Entity com.holland.gateway.domain.Log
 */
public interface LogMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(Log record);

    Log selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Log record);

    int updateByPrimaryKey(Log record);

    List<Log> list(int offset, int limit);

    Long count();
}




