package com.holland.gateway.mapper;

import com.holland.gateway.domain.Code;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.holland.gateway.domain.Code
 */
@Mapper
public interface CodeMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Code record);

    int insertSelective(Code record);

    Code selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Code record);

    int updateByPrimaryKey(Code record);

    List<Map<String, String>> all(String type);
}




