package com.holland.gateway.filter;

import org.springframework.web.server.WebSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedRequestBodyObject {

    private final static Map<String, String> sessionIdAndRequestBody = new ConcurrentHashMap<>();

    public static void put(WebSession session, String requestBody) {
        sessionIdAndRequestBody.put(session.getId(), requestBody);
    }

    public static String get(WebSession session) {
        return sessionIdAndRequestBody.get(session.getId());
    }

    /**
     * 不从被网关转发的请求就直接通过block的方式阻塞获得
     */
    public static String getOrDefault(WebSession session, String requestBody) {
        return sessionIdAndRequestBody.getOrDefault(session.getId(), requestBody);
    }
}
