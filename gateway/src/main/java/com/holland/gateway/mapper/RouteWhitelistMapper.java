package com.holland.gateway.mapper;

import com.holland.gateway.domain.RouteWhitelist;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * @Entity com.holland.gateway.domain.RouteWhitelist
 */
@Mapper
public interface RouteWhitelistMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(RouteWhitelist record);

    int updateByPrimaryKeySelective(RouteWhitelist record);

    Optional<RouteWhitelist> selectByUrl(String url);

    List<RouteWhitelist> all();
}




