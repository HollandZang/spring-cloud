package com.holland.gateway.common;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestUtil {

    public static String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst("holland_token");
    }

    public static String getLoginName(ServerHttpRequest request) {
        final String token = getToken(request);
        return token == null ? null : token.substring(0, token.length() - 10);
    }
}
