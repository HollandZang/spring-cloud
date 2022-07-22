package com.holland.gateway.filter;

import com.holland.gateway.swagger.SwaggerRouteFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 过滤admin模块健康日志
        if (exchange.getRequest().getURI().getRawPath().startsWith("/actuator")) return chain.filter(exchange);
        // 过滤swagger日志
        if ("true".equals(exchange.getRequest().getHeaders().getFirst(SwaggerRouteFilter.HEADER_NAME))) {
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