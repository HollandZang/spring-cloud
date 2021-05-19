package com.holland.gateway.common;

import com.holland.gateway.domain.RouteWhitelist;
import com.holland.gateway.mapper.RouteWhitelistMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomCache {

    @Resource
    private RouteWhitelistMapper routeWhitelistMapper;

    public final List<RouteWhitelist> routeWhitelist = new ArrayList<>();

    @PostConstruct
    public void init() {
        routeWhitelist.addAll(routeWhitelistMapper.all());
    }

}
