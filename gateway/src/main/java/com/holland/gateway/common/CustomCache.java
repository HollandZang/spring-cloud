package com.holland.gateway.common;

import com.holland.gateway.route_white_list.RouteWhitelistMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Component
public class CustomCache {

    @Resource
    private RouteWhitelistMapper routeWhitelistMapper;

    public final Set<String> URL_NOT_NEED_TOKEN = new HashSet<>();

    @PostConstruct
    public void init() {
        URL_NOT_NEED_TOKEN.addAll(routeWhitelistMapper.findAllEnabled());
    }

}
