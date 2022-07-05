package com.holland.gateway.common;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestUtil {

    public static String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst("HAuth");
    }

    public static void setLoginName(ServerHttpRequest request, String loginName) {
        request.getHeaders().add("_loginName", loginName);
    }

    public static String getLoginName(ServerHttpRequest request) {
        return request.getHeaders().getFirst("_loginName");
    }
}
