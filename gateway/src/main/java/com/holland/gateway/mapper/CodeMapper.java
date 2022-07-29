package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.Code;
import com.holland.common.enums.gateway.CodeTypeEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.holland.common.entity.gateway.Code
 */
@Mapper
public interface CodeMapper extends BaseMapper<Code> {

    default List<Code> getByCode_type_id(String codeTypeId) {
        return getByCode_type_id(CodeTypeEnum.find(codeTypeId));
    }

    default List<Code> getByCode_type_id(CodeTypeEnum codeTypeId) {
        return this.selectList(new QueryWrapper<Code>()
                .eq("code_type_id", codeTypeId));
    }
}
