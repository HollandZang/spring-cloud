package com.holland.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.Log;
import com.holland.common.entity.gateway.LogLogin;
import com.holland.common.entity.gateway.User;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.gateway.swagger.SwaggerRouteFilter;
import com.holland.gateway.swagger.SwaggerUtils;
import com.holland.kafka.Producer;
import com.holland.kafka.Topic;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class CustomWebFilterChain {

    @Resource
    private UserCache userCache;

    @Resource
    private SwaggerUtils swaggerUtils;

    @Resource
    private Producer kafkaProducer;

    @Resource
    private AuthCheckMapping authCheckMapping;

    private final Logger logger = LoggerFactory.getLogger(CustomWebFilterChain.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        final AuthCheckFilter authCheckFilter = new AuthCheckFilter(authCheckMapping, userCache);
        http
                .addFilterAt(SwaggerRouteFilter.getWebFilter(swaggerUtils), SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
//                .addFilterAt(corsFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterAt(authCheckFilter.filter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(logFilter(), SecurityWebFiltersOrder.LAST)
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

    private WebFilter corsFilter() {
        return (exchange, chain) -> {
            final ServerHttpResponse originalResponse = exchange.getResponse();
            final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    //修改header
                    final HttpHeaders httpHeaders = originalResponse.getHeaders();
                    httpHeaders.add("Access-Control-Allow-Origin", httpHeaders.getOrigin());
                    httpHeaders.add("Access-Control-Allow-Credentials", "true");
                    httpHeaders.add("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
                    httpHeaders.add("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, Authorization, HAuth");
                    return super.writeWith(body);
                }
            };
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }

    private WebFilter logFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String api = request.getURI().getRawPath();

            // 过滤admin模块健康日志
            if (api.startsWith("/actuator")) return chain.filter(exchange);
            // 过滤swagger日志
            if (swaggerUtils.enabledSwagger() && swaggerUtils.isSwaggerRequest(exchange.getRequest().getURI().getRawPath())) {
                return chain.filter(exchange);
            }

            //从这里统一的获取requestBody，不用区分网关和其他服务的差异
            final String requestBody = DataBufferUtils.join(request.getBody())
                    .map(reqDataBuffer -> reqDataBuffer.toString(StandardCharsets.UTF_8))
                    .block();
            final ServerHttpRequestDecorator serverHttpRequestDecorator;
            if (requestBody != null) {
                serverHttpRequestDecorator = new ServerHttpRequestDecorator(request) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        final NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                        final DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(requestBody.getBytes(StandardCharsets.UTF_8));
                        return Flux.just(bodyDataBuffer);
                    }
                };
            } else serverHttpRequestDecorator = null;

            final ServerHttpResponse originalResponse = exchange.getResponse();
            final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    //输出返回结果
                    if (body instanceof Flux || body instanceof Mono) {
                        final Mono<Void> newMono = super.writeWith(
                                DataBufferUtils.join(body)
                                        .doOnNext(dataBuffer -> {
                                            final String respBody = dataBuffer.toString(StandardCharsets.UTF_8);
                                            //输出body
                                            logger.debug("Response : status = {}, body = {}", exchange.getResponse().getStatusCode().toString(), respBody);

                                            switch (request.getURI().getRawPath()) {
                                                case "/user/login":
                                                    logLogin(request, originalResponse.getStatusCode(), respBody, requestBody);
                                                    break;
                                                case "/user/logout":
                                                    logLogout(request, originalResponse.getStatusCode(), respBody);
                                                    break;
                                                default:
                                                    log(request, originalResponse.getStatusCode(), respBody, requestBody);
                                            }
                                        })
                        );
                        return newMono;
                    }
                    return super.writeWith(body);
                }
            };

            logger.debug("{} {} {}", request.getMethod(), request.getURI(), requestBody);

            if (requestBody != null) {
                return chain.filter(exchange.mutate().request(serverHttpRequestDecorator).response(decoratedResponse).build());
            }
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }

    private void log(ServerHttpRequest request, HttpStatus statusCode, String respBody, String requestBody) {
        final CacheUser cacheUser = RequestUtil.getCacheUser(request);
        final String loginName = cacheUser == null ? null : cacheUser.getLogin_name();
        final String reqLine = request.getMethodValue() + " " + request.getURI().getRawPath();
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();
        final Map<String, String> m = request.getQueryParams().toSingleValueMap();
        final String queryParam = m.isEmpty() ? null : JSONObject.toJSONString(m);

        final Log log = new Log()
                .setOperateUser(loginName)
                .setTimestamp(System.currentTimeMillis())
                .setReqLine(reqLine)
                .setBody(requestBody)
                .setParam(queryParam)
                .setIp(ip)
                .setResCode(result)
                .setResData(respBody);

        try {
            kafkaProducer.exec(Topic.op_log, JSON.toJSONString(log));
        } catch (Exception e) {
            logger.error("log->'log'", e);
        }
    }

    private void logLogin(ServerHttpRequest request, HttpStatus statusCode, String respBody, String requestBody) {
        final String from = request.getHeaders().getFirst("User-Agent");
        final String loginName = JSONObject.parseObject(requestBody, User.class).getLogin_name();
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();

        final LogLogin logLogin = new LogLogin()
                .setLoginName(loginName)
                .setPwd(null)
                .setTimestamp(System.currentTimeMillis())
                .setActionType("login")
                .setFrom(from)
                .setIp(ip)
                .setResCode(result)
                .setResBody(respBody);

        try {
            kafkaProducer.exec(Topic.login_log, JSON.toJSONString(logLogin));
        } catch (Exception e) {
            logger.error("log->'logLogin'", e);
        }
    }

    private void logLogout(ServerHttpRequest request, HttpStatus statusCode, String respBody) {
        final CacheUser cacheUser = RequestUtil.getCacheUser(request);
        final String loginName = cacheUser == null ? null : cacheUser.getLogin_name();
        final String ip = request.getRemoteAddress() == null ? null : request.getRemoteAddress().toString();
        final int result = statusCode.value();

        final LogLogin logLogin = new LogLogin()
                .setLoginName(loginName)
                .setTimestamp(System.currentTimeMillis())
                .setActionType("logout")
                .setIp(ip)
                .setResCode(result)
                .setResBody(respBody);

        try {
            kafkaProducer.exec(Topic.login_log, JSON.toJSONString(logLogin));
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
