package com.holland.gateway.route_white_list;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RouteWhitelistMapper {

    @Select("select url from config.public.route_whitelist where enabled = true")
    List<String> findAllEnabled();

    @InsertProvider(type = RouteWhitelistSqlFactory.class, method = "insert")
    int insert(RouteWhitelist routeWhitelist);

    @UpdateProvider(type = RouteWhitelistSqlFactory.class, method = "update")
    int update(RouteWhitelist routeWhitelist);

    @Delete("delete from config.public.route_whitelist where id = #{id}")
    int del(Integer id);

    @Select("select id, url, enabled from config.public.route_whitelist where url = #{url}")
    Optional<RouteWhitelist> getByUrl(String url);
}
