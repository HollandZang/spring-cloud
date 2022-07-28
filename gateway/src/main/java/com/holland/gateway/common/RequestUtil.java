package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.aggregate.CacheUser;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestUtil {

    public static String AUTH_KEY = "HAuth";
    public static String USER_KEY = "_user";

    public static UserCache userCache;

    public static void init(UserCache c) {
        RequestUtil.userCache = c;
    }

    public static String getReqLine(ServerHttpRequest request) {
        return request.getMethodValue() + " " + request.getURI().getRawPath();
    }

    public static String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst(AUTH_KEY);
    }

    public static ServerHttpRequest setCacheUser(ServerHttpRequest request) {
        final String cacheUser = RequestUtil.getCacheUserStr(request);
        if (cacheUser != null) {
            request = request.mutate().header(USER_KEY, new String[]{cacheUser}).build();
        }
        return request;
    }

    public static CacheUser getCacheUser(ServerHttpRequest request) {
        final String user = request.getHeaders().getFirst(USER_KEY);
        if (user != null) {
            return JSON.parseObject(user, CacheUser.class);
        } else {
            final String token = getToken(request);
            return token == null ? null : userCache.get(token);
        }
    }

    public static String getCacheUserStr(ServerHttpRequest request) {
        final String user = request.getHeaders().getFirst(USER_KEY);
        if (user != null) {
            return user;
        } else {
            final String token = getToken(request);
            return token == null ? null : JSON.toJSONString(userCache.get(token));
        }
    }
}
