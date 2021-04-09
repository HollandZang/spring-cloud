package com.holland.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Order(-1)
//@Component
public class PostFilter implements GlobalFilter {

    private final Logger log = LoggerFactory.getLogger(PostFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();

        log.info("start PostFilter-------------------------------------------------");
        log.info("HttpMethod:{},Url:{}", request.getMethod(), request.getURI().getRawPath());
        if (request.getMethod() == HttpMethod.GET) {
            log.info("end-------------------------------------------------");
        }
        return chain.filter(exchange);
    }
}
