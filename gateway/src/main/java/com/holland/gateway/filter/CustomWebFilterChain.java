package com.holland.gateway.filter;

import com.holland.gateway.common.CustomCache;
import com.holland.gateway.common.RedisUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class CustomWebFilterChain {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private CustomCache customCache;

    private final Logger logger = LoggerFactory.getLogger(CustomWebFilterChain.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .addFilterAfter(tokenFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterBefore(logFilter(), SecurityWebFiltersOrder.LAST)
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
//                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }

    private WebFilter tokenFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final List<String> tokens = request.getHeaders().get("holland_token");
            final String token;
            if (!CollectionUtils.isEmpty(tokens) && tokens.size() >= 1 && StringUtils.hasText(tokens.get(0))) {
                token = tokens.get(0);
            } else {
                token = null;
            }

            /* 精确的路径匹配模式 */
            final boolean notNeedToken = customCache.URL_NOT_NEED_TOKEN.stream().anyMatch(it -> it.equals(request.getURI().getRawPath()));
            final ServerHttpResponse originalResponse = exchange.getResponse();

            //token验证
            if (!notNeedToken) {
                //token验证: 需要token的接口没传token
                if (token == null) {
                    originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return originalResponse.setComplete();
                }
                //token验证: token有效性
                final Object auth = redisUtil.getToken(token);
                if (auth == null) {
                    originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return originalResponse.setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }

    private WebFilter logFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            logger.debug("{} {}", request.getMethod(), request.getURI());

            final ServerHttpResponse originalResponse = exchange.getResponse();
            final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    //修改header
//                final HttpHeaders httpHeaders = originalResponse.getHeaders();
//                httpHeaders.add("userCredential", "aaaaaa");
                    //输出返回结果
                    if (body instanceof Flux || body instanceof Mono) {
                        final Mono<Void> newMono = super.writeWith(
                                DataBufferUtils.join(body)
                                        .doOnNext(dataBuffer -> {
                                            String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                                            //输出body
                                            logger.debug("Response : status = {}, body = {}", exchange.getResponse().getStatusCode().toString(), respBody);
                                        })
                        );
                        return newMono;
                    }
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }
}