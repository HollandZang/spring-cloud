package com.holland.gateway.filter;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

//@Component
public class GatewayFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(GatewayFilter.class);

    @Override
    public int getOrder() {
        // 控制在NettyWriteResponseFilter后执行
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
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
                if (body instanceof Flux) {
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
    }
}