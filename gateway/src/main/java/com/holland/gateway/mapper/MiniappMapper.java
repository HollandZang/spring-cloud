package com.holland.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.holland.common.entity.gateway.CodeType;
import com.holland.gateway.controller.Miniapp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MiniappMapper  extends BaseMapper<Miniapp> {
}
