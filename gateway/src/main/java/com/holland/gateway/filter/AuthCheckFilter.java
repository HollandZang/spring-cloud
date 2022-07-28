package com.holland.gateway.filter;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthCheckFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);
    private boolean checkByAnnotation = true;

    private final AuthCheckMapping authCheckMapping;
    private final List<Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>>> checkHandler;

    public AuthCheckFilter(AuthCheckMapping authCheckMapping) {
        this.authCheckMapping = authCheckMapping;

        checkHandler = new ArrayList<>();
        checkHandler.add(checkToken);
        checkHandler.add(checkAdmin);
    }

    public WebFilter filterByAnnotation() {
        logger.info("AuthCheckFilter by {}", checkByAnnotation ? "Annotation" : "Properties");
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String reqLine = request.getMethodValue() + " " + request.getURI().getRawPath();
            final List<AuthCheck.AuthCheckEnum> enums = authCheckMapping.get(reqLine);
            if (enums != null && enums.size() > 0) {
                final StringBuilder builder;
                if (logger.isDebugEnabled()) {
                    final String token = RequestUtil.getToken(request);
                    final CacheUser cacheUser = RequestUtil.getCacheUser(request);
                    builder = new StringBuilder(request.getId() + " authCheck: reqLine=" + reqLine + " , token=" + token + ", loginName=" + (cacheUser == null ? null : cacheUser.getLogin_name()) + ", ");
                } else {
                    builder = null;
                }
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
                    logger.trace(builder.toString());
            }
            return chain.filter(exchange);
        };
    }

    public WebFilter filterByProperties(String group, ConfigService configService) throws NacosException {
        checkByAnnotation = false;
        NacosProp.gateway_router.forEach(setAuthCheckMappingByNacos(authCheckMapping));
        NacosProp.listen(configService, group, "gateway_router", properties -> properties.forEach(setAuthCheckMappingByNacos(authCheckMapping)));
        return filterByAnnotation();
    }

    private BiConsumer<Object, Object> setAuthCheckMappingByNacos(AuthCheckMapping authCheckMapping) {
        authCheckMapping.clear();
        return (k, v) -> {
            final String s = v.toString();
            if (s.isBlank()) return;
            final List<AuthCheck.AuthCheckEnum> enums = Arrays.stream(s.split(","))
                    .map(auth -> AuthCheck.AuthCheckEnum.valueOf(auth.toUpperCase()))
                    .collect(Collectors.toList());
            authCheckMapping.put(k.toString(), enums);
        };
    }

    private final Function<ServerHttpRequest, Function<StringBuilder, Function<List<AuthCheck.AuthCheckEnum>, HttpStatus>>> checkToken = request -> builder -> authCheck -> {
        final Optional<AuthCheck.AuthCheckEnum> o = authCheck.stream().filter(v -> v.equals(AuthCheck.AuthCheckEnum.TOKEN)).findAny();
        if (o.isPresent()) {
            final CacheUser cacheUser = RequestUtil.getCacheUser(request);
            if (cacheUser == null) {
                if (logger.isDebugEnabled())
                    builder.append(", checkToken=ERR");
                return HttpStatus.UNAUTHORIZED;
            }
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
            if (!"admin".equals(cacheUser.getRoles())) {
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
