package com.holland.gateway.swagger;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

public class SwaggerRouteFilter {
    public static final String HEADER_NAME = "swagger-req";

    public static WebFilter getWebFilter(SwaggerUtils swaggerUtils) {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String path = request.getURI().getPath();
            if (swaggerUtils.enabledSwagger() && swaggerUtils.isSwaggerRequest(path)) {
                final ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, "true").build();
                final ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
                return chain.filter(newExchange);
            }

            return chain.filter(exchange);
        };
    }
}
