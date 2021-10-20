package com.holland.gateway.mapper;

import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.domain.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.gateway.domain.Log
 */
@Mapper
public interface LogMapper {

    int insertSelective(Log record);

    Log selectByPrimaryKey(Long id);

    List<Log> list(PageHelper pageHelper);

    Long count();
}




