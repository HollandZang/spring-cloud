package com.holland.gateway;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Order(-1)
@Component
public class PreFilter implements GlobalFilter {

    private final Logger logger = LoggerFactory.getLogger(PreFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();

        logger.info("start PreFilter-------------------------------------------------");
        logger.info("HttpMethod:{},Url:{}", request.getMethod(), request.getURI().getRawPath());
        if (request.getMethod() == HttpMethod.GET) {
            logger.info("end-------------------------------------------------");
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                //修改header
                HttpHeaders httpHeaders = originalResponse.getHeaders();
                httpHeaders.add("userCredential", "aaaaaa");
                //输出返回结果
                if (body instanceof Flux) {
                    Mono<Void> newMono = super.writeWith(
                            DataBufferUtils.join(body)
                                    .doOnNext(dataBuffer -> {
                                        String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                                        //输出body
                                        logger.info("fgwResponse : body = {}", respBody);
                                    })
                    );
                    //输出response，不包含body
//                    logger.info("fgwResponse : resp = {}", JSON.toJSONString(exchange.getResponse()));
                    logger.info("fgwResponse : resp = {}", exchange.getResponse());
                    return newMono;

                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            System.out.println("RUN POST");
            exchange.mutate().response(decoratedResponse).build();
        }));
    }
}