package com.holland.gateway.mapper;

import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.common.entity.gateway.CodeType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.common.entity.gateway.CodeType
 */
@Mapper
public interface CodeTypeMapper {

    int deleteByPrimaryKey(Long id);

    int insert(CodeType record);

    int updateByPrimaryKey(CodeType record);

    List<CodeType> list(PageHelper pageHelper);

    Long count();
}




