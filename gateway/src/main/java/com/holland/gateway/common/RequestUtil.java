package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.aggregate.CacheUser;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestUtil {

    public static UserCache userCache;

    public static void init(UserCache c) {
        RequestUtil.userCache = c;
    }

    public static String getToken(ServerHttpRequest request) {
        return request.getHeaders().getFirst("HAuth");
    }

    public static void setLoginName(ServerHttpRequest request, CacheUser cacheUser) {
        request.getHeaders().add("_user", JSON.toJSONString(cacheUser));
    }

    public static CacheUser getCacheUser(ServerHttpRequest request) {
        final String user = request.getHeaders().getFirst("_user");
        if (user != null) {
            return JSON.parseObject(user, CacheUser.class);
        } else {
            return userCache.get(getToken(request));
        }
    }
}
