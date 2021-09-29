package com.holland.gateway.filter;

import com.holland.gateway.swagger.SwaggerUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 官方推荐用ModifyRequestBodyGatewayFilterFactory来获取body
 * https://cloud.spring.io/spring-cloud-gateway/multi/multi__gatewayfilter_factories.html
 */
//@Component
public class CacheRequestBodyFilter implements GlobalFilter, Ordered {

    @Resource
    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter;

    @Resource
    private SwaggerUtils swaggerUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 过滤swagger日志
        if (swaggerUtils.enabledSwagger() && swaggerUtils.isSwaggerRequest(exchange.getRequest().getURI().getRawPath())) {
            return chain.filter(exchange);
        }

        return modifyRequestBodyFilter
                .apply(
                        new ModifyRequestBodyGatewayFilterFactory.Config()
                                .setRewriteFunction(String.class, String.class
                                        , (serverWebExchange, body) -> {
                                            //此处可修改body的值
                                            exchange.getSession().subscribe(session -> CachedRequestBodyObject.put(session, body));
                                            return Mono.just(body);
                                        })
                )
                .filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}