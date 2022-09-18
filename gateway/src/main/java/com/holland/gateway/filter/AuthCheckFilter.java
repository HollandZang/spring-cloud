package com.holland.gateway.filter;

import com.alibaba.nacos.api.exception.NacosException;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.Code;
import com.holland.common.enums.gateway.CodeTypeEnum;
import com.holland.common.enums.gateway.RoleEnum;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.conf.NacosProp;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.nacos.conf.NacosPropKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebFilter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class AuthCheckFilter {

    private final Logger logger = LoggerFactory.getLogger(AuthCheckFilter.class);
    private boolean checkByAnnotation = true;

    private final AuthCheckMapping authCheckMapping;
    private final Map<String, FnCheck> checkHandler;

    public AuthCheckFilter(AuthCheckMapping authCheckMapping, CodeMapper codeMapper) {
        this.authCheckMapping = authCheckMapping;

        final FnCheck checkToken = (cacheUser, builder, authCheck, key) -> authCheck.stream()
                .filter(v -> v.equals(RoleEnum.TOKEN))
                .findAny()
                .map(o -> {
                    if (cacheUser == null) {
                        if (logger.isDebugEnabled())
                            builder.append(", check token=ERR");
                        return HttpStatus.UNAUTHORIZED;
                    }
                    if (logger.isDebugEnabled())
                        builder.append(", check token=OK");
                    return null;
                })
                .orElse(null);
        final FnCheck checkKeyRole = (cacheUser, builder, authCheck, key) -> authCheck.stream()
                .filter(v -> v.equals(RoleEnum.find(key)))
                .findAny()
                .map(o -> {
                    if (cacheUser == null) {
                        if (logger.isDebugEnabled())
                            builder.append(", check token=ERR");
                        return HttpStatus.UNAUTHORIZED;
                    }
                    if (cacheUser.getRoles() == null || !cacheUser.getRoles().contains(key)) {
                        if (logger.isDebugEnabled())
                            builder.append(", check ").append(key).append("=ERR");
                        return HttpStatus.FORBIDDEN;
                    }
                    if (logger.isDebugEnabled())
                        builder.append(", check ").append(key).append("=OK");
                    return null;
                })
                .orElse(null);

        checkHandler = new LinkedHashMap<>();
        checkHandler.put("token", checkToken);
        for (Code code : codeMapper.getByCode_type_id(CodeTypeEnum.ROLE)) {
            if (code.getVal1() == null) {
                checkHandler.put(code.getVal(), checkKeyRole);
            } else {
                /* special rules */
                logger.error("not impl: AuthCheckFilter -> {}", code.getVal());
            }
        }
    }

    public WebFilter filterByAnnotation() {
        logger.info("AuthCheckFilter by {}", checkByAnnotation ? "Annotation" : "Properties");
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String reqLine = request.getMethodValue() + " " + request.getURI().getRawPath();
            final List<RoleEnum> enums = authCheckMapping.get(reqLine);
            if (enums != null && enums.size() > 0) {
                final StringBuilder builder;
                final CacheUser cacheUser = RequestUtil.getCacheUser(request);
                if (logger.isDebugEnabled()) {
                    final String token = RequestUtil.getToken(request);
                    builder = new StringBuilder(request.getId() + " authCheck: reqLine=" + reqLine + " , token=" + token + ", loginName=" + (cacheUser == null ? null : cacheUser.getLogin_name()) + ", ");
                } else {
                    builder = null;
                }
                final ServerHttpResponse originalResponse = exchange.getResponse();

                for (Map.Entry<String, FnCheck> entry : checkHandler.entrySet()) {
                    final String key = entry.getKey();
                    final FnCheck fnCheck = entry.getValue();
                    final HttpStatus httpStatus = fnCheck.apply(cacheUser, builder, enums, key);
                    if (httpStatus != null) {
                        if (logger.isDebugEnabled())
                            //noinspection ConstantConditions
                            logger.debug(builder.toString());
                        originalResponse.setStatusCode(httpStatus);
                        return originalResponse.setComplete();
                    }
                }
                if (logger.isDebugEnabled())
                    //noinspection ConstantConditions
                    logger.trace(builder.toString());
            }
            return chain.filter(exchange);
        };
    }

    public WebFilter filterByProperties() {
        checkByAnnotation = false;
        final BiConsumer<Object, Object> authCheckMappingByNacos = setAuthCheckMappingByNacos(authCheckMapping);
        NacosProp.gateway_router.forEach(authCheckMappingByNacos);
        try {
            NacosPropKit.listen("gateway_router", properties -> properties.forEach(authCheckMappingByNacos));
        } catch (NacosException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return filterByAnnotation();
    }

    private BiConsumer<Object, Object> setAuthCheckMappingByNacos(AuthCheckMapping authCheckMapping) {
        authCheckMapping.clear();
        return (k, v) -> {
            final String s = v.toString();
            final List<RoleEnum> enums;
            if (s.isEmpty()) {
                enums = null;
            } else {
                enums = Arrays.stream(s.split(","))
                        .map(RoleEnum::find)
                        .collect(Collectors.toList());
            }
            authCheckMapping.put(k.toString(), enums);
        };
    }

    @FunctionalInterface
    interface FnCheck {
        HttpStatus apply(CacheUser cacheUser, StringBuilder builder, List<RoleEnum> authCheck, String key);
    }

}
