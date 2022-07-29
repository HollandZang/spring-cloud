package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeTypeId;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.common.entity.gateway.Code
 */
@Mapper
public interface CodeMapper extends BaseMapper<Code> {

    default List<Code> getByCode_type_id(String codeTypeId) {
        return getByCode_type_id(CodeTypeId.ROLE);
    }

    default List<Code> getByCode_type_id(CodeTypeId codeTypeId) {
        return this.selectList(new QueryWrapper<Code>()
                .eq("code_type_id", codeTypeId));
    }
}
