package com.holland.gateway.route_white_list;

import org.apache.ibatis.jdbc.SQL;

public class RouteWhitelistSqlFactory {

    public String insert(RouteWhitelist routeWhitelist) {
        final SQL sql = new SQL()
                .INSERT_INTO("\"route_whitelist\"");
        sql.VALUES("\"url\"", "'" + routeWhitelist.getUrl() + "'");
        if (routeWhitelist.getEnabled() != null) {
            sql.VALUES("\"enabled\"", "'" + routeWhitelist.getEnabled().toString() + "'");
        }
        return sql.toString();
    }

    public String update(RouteWhitelist routeWhitelist) {
        final SQL sql = new SQL()
                .UPDATE("\"route_whitelist\"");

        if (routeWhitelist.getUrl() != null && !routeWhitelist.getUrl().isEmpty()) {
            sql.SET("\"url\" = '" + routeWhitelist.getUrl() + "'");
        }

        if (routeWhitelist.getEnabled() != null) {
            sql.SET("\"enabled\" = '" + routeWhitelist.getEnabled() + "'");
        }

        return sql.WHERE("\"id\" = '" + routeWhitelist.getId() + "'")
                .toString();
    }

}
