package com.holland.gateway;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomCache {
    @Resource
    private RouteWhitelistRepo routeWhitelistRepo;

    public static final List<String> URL_NOT_NEED_TOKEN = new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("INIT");
        URL_NOT_NEED_TOKEN.add("/filesystem/test");
        final Iterable<Object> all = routeWhitelistRepo.findAll();
        all.forEach(System.out::println);
    }

}
