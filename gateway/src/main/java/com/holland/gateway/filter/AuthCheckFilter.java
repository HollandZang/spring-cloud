package com.holland.gateway.filter;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeTypeId;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.AuthCheckMapping;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.conf.NacosProp;
import com.holland.gateway.mapper.CodeMapper;
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
                .filter(v -> v.equals(AuthCheck.AuthCheckEnum.TOKEN))
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
                .filter(v -> v.equals(AuthCheck.AuthCheckEnum.valueOf(key.toUpperCase())))
                .findAny()
                .map(o -> {
                    if (cacheUser == null) {
                        if (logger.isDebugEnabled())
                            builder.append(", check token=ERR");
                        return HttpStatus.UNAUTHORIZED;
                    }
                    if (cacheUser.getRoles() == null || !cacheUser.getRoles().contains(key)) {
                        if (logger.isDebugEnabled())
                            builder.append(", check " + key + "=ERR");
                        return HttpStatus.FORBIDDEN;
                    }
                    if (logger.isDebugEnabled())
                        builder.append(", check " + key + "=OK");
                    return null;
                })
                .orElse(null);

        checkHandler = new LinkedHashMap<>();
        checkHandler.put("token", checkToken);
        for (Code code : codeMapper.getByCode_type_id(CodeTypeId.ROLE)) {
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
            final List<AuthCheck.AuthCheckEnum> enums = authCheckMapping.get(reqLine);
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
        final BiConsumer<Object, Object> authCheckMappingByNacos = setAuthCheckMappingByNacos(authCheckMapping);
        NacosProp.gateway_router.forEach(authCheckMappingByNacos);
        NacosProp.listen(configService, group, "gateway_router", properties -> properties.forEach(authCheckMappingByNacos));
        return filterByAnnotation();
    }

    private BiConsumer<Object, Object> setAuthCheckMappingByNacos(AuthCheckMapping authCheckMapping) {
        authCheckMapping.clear();
        return (k, v) -> {
            final String s = v.toString();
            final List<AuthCheck.AuthCheckEnum> enums;
            if (s.isBlank()) {
                enums = null;
            } else {
                enums = Arrays.stream(s.split(","))
                        .map(auth -> AuthCheck.AuthCheckEnum.valueOf(auth.toUpperCase()))
                        .collect(Collectors.toList());
            }
            authCheckMapping.put(k.toString(), enums);
        };
    }

    interface FnCheck {
        HttpStatus apply(CacheUser cacheUser, StringBuilder builder, List<AuthCheck.AuthCheckEnum> authCheck, String key);
    }

}
