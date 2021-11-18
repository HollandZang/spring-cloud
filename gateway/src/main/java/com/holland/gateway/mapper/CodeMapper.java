package com.holland.gateway.mapper;

import com.holland.common.entity.gateway.Code;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.holland.common.entity.gateway.Code
 */
@Mapper
public interface CodeMapper {

    int delete(String type, String code);

    int insert(Code record);

    int updateByPrimaryKey(Code record);

    List<Map<String, String>> all(String type);
}




