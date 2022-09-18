package com.holland.gateway.conf;

import com.holland.nacos.conf.NacosConf;

import java.util.Properties;

public class NacosProp {
    @NacosConf(group = "gateway")
    public static Properties gateway;

    @NacosConf(group = "gateway")
    public static Properties gateway_router;

    @NacosConf(group = "common")
    public static Properties common_admin;
}
