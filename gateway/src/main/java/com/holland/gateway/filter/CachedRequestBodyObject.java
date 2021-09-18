package com.holland.gateway.filter;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebSession;

import java.nio.charset.StandardCharsets;
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
    public static String getOrBlock(WebSession session, ServerHttpRequest request) {
        final String s = sessionIdAndRequestBody.get(session.getId());
        return s != null ? s :
                DataBufferUtils.join(request.getBody())
                        .map(dataBuffer -> dataBuffer.toString(StandardCharsets.UTF_8))
                        .block();
    }
}
