package com.holland.gateway.filter;

import com.holland.common.aggregate.CacheUser;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AuthCheckFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);

    private UserCache userCache;
    private final AuthCheckMapping authCheckMapping;
    private final List<Function<ServerHttpRequest, Function<StringBuilder, Function<AuthCheck, HttpStatus>>>> checkHandler;

    public AuthCheckFilter(AuthCheckMapping authCheckMapping, UserCache userCache) {
        this.authCheckMapping = authCheckMapping;
        this.userCache = userCache;

        checkHandler = new ArrayList<>();
        checkHandler.add(checkToken);
        checkHandler.add(checkAdmin);
    }

    public WebFilter filter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String reqLine = request.getMethodValue() + " " + request.getURI().getRawPath();
            final AuthCheck authCheck = authCheckMapping.get(reqLine);
            if (authCheck != null) {
                final StringBuilder builder = new StringBuilder("authCheck: ");
                final ServerHttpResponse originalResponse = exchange.getResponse();

                for (Function<ServerHttpRequest, Function<StringBuilder, Function<AuthCheck, HttpStatus>>> function : checkHandler) {
                    final HttpStatus httpStatus = function.apply(request).apply(builder).apply(authCheck);
                    if (httpStatus != null) {
                        logger.debug(builder.toString());
                        originalResponse.setStatusCode(httpStatus);
                        return originalResponse.setComplete();
                    }
                }
                logger.debug(builder.toString());
            }
            return chain.filter(exchange);
        };
    }

    private final Function<ServerHttpRequest, Function<StringBuilder, Function<AuthCheck, HttpStatus>>> checkToken = request -> builder -> authCheck -> {
        final AuthCheck.AuthCheckEnum[] values = authCheck.values();
        final Optional<AuthCheck.AuthCheckEnum> o = Arrays.stream(values).filter(v -> v.equals(AuthCheck.AuthCheckEnum.TOKEN)).findAny();
        if (o.isPresent()) {
            final String token = RequestUtil.getToken(request);
            //token验证: 需要token的接口没传token
            if (token == null) {
                builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            //token验证: token有效性
            final CacheUser cacheUser = userCache.get(token);
            if (cacheUser == null) {
                builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            RequestUtil.setCacheUser(request, cacheUser);
            builder.append(", checkToken=OK");
        }
        return null;
    };

    private final Function<ServerHttpRequest, Function<StringBuilder, Function<AuthCheck, HttpStatus>>> checkAdmin = request -> builder -> authCheck -> {
        final AuthCheck.AuthCheckEnum[] values = authCheck.values();
        final Optional<AuthCheck.AuthCheckEnum> o = Arrays.stream(values).filter(v -> v.equals(AuthCheck.AuthCheckEnum.ADMIN)).findAny();
        if (o.isPresent()) {
            final CacheUser cacheUser = RequestUtil.getCacheUser(request);
            if (cacheUser == null) {
                builder.append(", checkAdmin=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            if (!"admin".equals(cacheUser.getRole())) {
                builder.append(", checkAdmin=ERR");
                return HttpStatus.FORBIDDEN;
            }
            builder.append(", checkAdmin=OK");
        }
        return null;
    };

}
