package com.holland.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.holland.gateway.common.RedisController;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.domain.Log;
import com.holland.gateway.domain.LogLogin;
import com.holland.gateway.mapper.LogLoginMapper;
import com.holland.gateway.mapper.LogMapper;
import com.holland.gateway.mapper.RouteWhitelistMapper;
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
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Configuration
@EnableWebFluxSecurity
public class CustomWebFilterChain {

    @Resource
    private RedisController redisController;

    @Resource
    private RouteWhitelistMapper routeWhitelistMapper;

    @Resource
    private LogMapper logMapper;

    @Resource
    private LogLoginMapper logLoginMapper;

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

            /* ??????????????????????????? */
//            final boolean notNeedToken = routeWhitelistMapper.all().stream()
//                    .filter(RouteWhitelist::getEnabled)
//                    .anyMatch(it -> it.getUrl().equals(request.getURI().getRawPath()));
//            final ServerHttpResponse originalResponse = exchange.getResponse();
//
//            //token??????
//            if (!notNeedToken) {
//                final String token = RequestUtil.getToken(request);
//                //token??????: ??????token???????????????token
//                if (token == null) {
//                    originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return originalResponse.setComplete();
//                }
//                //token??????: token?????????
//                final Object auth = redisUtil.getToken(token);
//                if (auth == null) {
//                    originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return originalResponse.setComplete();
//                }
//            }
            return chain.filter(exchange);
        };
    }

    private WebFilter logFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String api = request.getURI().getRawPath();
            if (api.startsWith("/actuator")) {
                // ??????admin??????????????????
                return chain.filter(exchange);
            }
            logger.debug("{} {}", request.getMethod(), request.getURI());

            final ServerHttpResponse originalResponse = exchange.getResponse();
            final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    //??????header
//                final HttpHeaders httpHeaders = originalResponse.getHeaders();
//                httpHeaders.add("userCredential", "aaaaaa");
                    //??????????????????
                    if (body instanceof Flux || body instanceof Mono) {
                        final Mono<Void> newMono = super.writeWith(
                                DataBufferUtils.join(body)
                                        .doOnNext(dataBuffer -> {
                                            String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                                            //??????body
                                            logger.debug("Response : status = {}, body = {}", exchange.getResponse().getStatusCode().toString(), respBody);

                                            switch (request.getURI().getRawPath()) {
                                                case "/user/login":
                                                    logLogin(request, originalResponse.getStatusCode(), respBody);
                                                    break;
                                                case "/user/logout":
                                                    logLogout(request, originalResponse.getStatusCode(), respBody);
                                                    break;
                                                default:
                                                    log(request, originalResponse.getStatusCode(), respBody, exchange);
                                            }
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

    private void log(ServerHttpRequest request, HttpStatus statusCode, String respBody, ServerWebExchange exchange) {
        final String loginName = RequestUtil.getLoginName(request);
        final String type = request.getMethodValue();
        final String api = request.getURI().getRawPath();
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();

        final Log log = new Log()
                .setOperateUser(loginName)
                .setOperateTime(new Date())
                .setOperateType(type)
                .setOperateApi(api)
                .setIp(ip)
                .setResult(result)
                .setResponse(respBody);

        if ("DELETE".equals(type)) {
            final int index = api.lastIndexOf("/");
            logMapper.insertSelective(
                    log.setOperateApi(api.substring(0, index)).setParam(api.substring(index + 1))
            );
        }

        exchange.getSession().subscribe(session -> {
            final String bodyParam = CachedRequestBodyObject.getOrBlock(session, request);
            final String queryParam = JSONObject.toJSONString(request.getQueryParams().toSingleValueMap());
            final String param = queryParam + (bodyParam == null ? "" : bodyParam);

            try {
                logMapper.insertSelective(
                        log.setParam(truncByte(param, 1024)));
            } catch (Exception e) {
                logger.error("log->'log'", e);
            }
        });
    }

    private void logLogin(ServerHttpRequest request, HttpStatus statusCode, String respBody) {
        /*???????????????????????????????????????*/
        final String from = request.getHeaders().getFirst("User-Agent");
        final String loginName = request.getQueryParams().getFirst("loginName");
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();

        try {
            logLoginMapper.insertSelective(new LogLogin()
                    .setOperateUser(loginName)
                    .setOperateTime(new Date())
                    .setOperateType("1")
                    .setFrom(from)
                    .setIp(ip)
                    .setResult(result)
                    .setResponse(respBody));
        } catch (Exception e) {
            logger.error("log->'logLogin'", e);
        }
    }

    private void logLogout(ServerHttpRequest request, HttpStatus statusCode, String respBody) {
        final String loginName = RequestUtil.getLoginName(request);
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();

        try {
            logLoginMapper.insertSelective(new LogLogin()
                    .setOperateUser(loginName)
                    .setOperateTime(new Date())
                    .setOperateType("0")
                    .setIp(ip)
                    .setResult(result)
                    .setResponse(respBody));
        } catch (Exception e) {
            logger.error("log->'logLogout'", e);
        }
    }

    private String truncByte(String field, int length) {
        if (field == null) {
            return null;
        }
        int len = 0;
        char[] charArray = field.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            len += c > 127 || c == 97 ? 2 : 1;
            if (len == length - 3 || len == length - 4) return field.substring(0, i) + "...";
        }
        return field;
    }
}
