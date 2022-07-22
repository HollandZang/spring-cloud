package com.holland.gateway.filter;

import com.holland.common.aggregate.CacheUser;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.gateway.conf.NacosProp;
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
import java.util.stream.Collectors;

public class AuthCheckFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);

    private UserCache userCache;
    private final List<Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>>> checkHandler;

    public AuthCheckFilter(UserCache userCache) {
        this.userCache = userCache;

        checkHandler = new ArrayList<>();
        checkHandler.add(checkToken);
        checkHandler.add(checkAdmin);
    }

    public WebFilter filterByAnnotation(AuthCheckMapping authCheckMapping) {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String reqLine = request.getMethodValue() + " " + request.getURI().getRawPath();
            final List<AuthCheck.AuthCheckEnum> enums = authCheckMapping.get(reqLine);
            if (enums != null && enums.size() > 0) {
                final StringBuilder builder = logger.isDebugEnabled() ? new StringBuilder("authCheck: ") : null;
                final ServerHttpResponse originalResponse = exchange.getResponse();

                for (Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>> function : checkHandler) {
                    final HttpStatus httpStatus = function.apply(request).apply(builder).apply(enums);
                    if (httpStatus != null) {
                        if (logger.isDebugEnabled())
                            logger.debug(builder.toString());
                        originalResponse.setStatusCode(httpStatus);
                        return originalResponse.setComplete();
                    }
                }
                if (logger.isDebugEnabled())
                    logger.debug(builder.toString());
            }
            return chain.filter(exchange);
        };
    }

    public WebFilter filterByProperties() {
        final AuthCheckMapping authCheckMapping = new AuthCheckMapping(NacosProp.gateway_router.size());
        NacosProp.gateway_router.forEach((k, v) -> {
            final String s = v.toString();
            if (s.isBlank()) return;
            final List<AuthCheck.AuthCheckEnum> enums = Arrays.stream(s.split(","))
                    .map(auth -> AuthCheck.AuthCheckEnum.valueOf(auth.toUpperCase()))
                    .collect(Collectors.toList());
            authCheckMapping.put(k.toString(), enums);
        });
        return filterByAnnotation(authCheckMapping);
    }

    private final Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>> checkToken = request -> builder -> authCheck -> {
        final Optional<AuthCheck.AuthCheckEnum> o = authCheck.stream().filter(v -> v.equals(AuthCheck.AuthCheckEnum.TOKEN)).findAny();
        if (o.isPresent()) {
            final String token = RequestUtil.getToken(request);
            //token验证: 需要token的接口没传token
            if (token == null) {
                if (logger.isDebugEnabled())
                    builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            //token验证: token有效性
            final CacheUser cacheUser = userCache.get(token);
            if (cacheUser == null) {
                if (logger.isDebugEnabled())
                    builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            RequestUtil.setCacheUser(request, cacheUser);
            if (logger.isDebugEnabled())
                builder.append(", checkToken=OK");
        }
        return null;
    };

    private final Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>> checkAdmin = request -> builder -> authCheck -> {
        final Optional<AuthCheck.AuthCheckEnum> o = authCheck.stream().filter(v -> v.equals(AuthCheck.AuthCheckEnum.ADMIN)).findAny();
        if (o.isPresent()) {
            final CacheUser cacheUser = RequestUtil.getCacheUser(request);
            if (cacheUser == null) {
                if (logger.isDebugEnabled())
                    builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
            if (!"admin".equals(cacheUser.getRole())) {
                if (logger.isDebugEnabled())
                    builder.append(", checkAdmin=ERR");
                return HttpStatus.FORBIDDEN;
            }
            if (logger.isDebugEnabled())
                builder.append(", checkAdmin=OK");
        }
        return null;
    };

}
