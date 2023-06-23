package com.holland.common.spring.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Slf4j
@Configuration
public class NacosConfig implements ApplicationListener<WebServerInitializedEvent> {
    @Value("${spring.application.name}")
    private String name;

    @Resource
    private NacosDiscoveryProperties nacosProperties;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        System.err.println("-----NacosConfig----");
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取局域网ip失败!", e);
            return;
        }
        WebServer server = event.getWebServer();
        int port = server.getPort();

        Map<String, String> metadata = nacosProperties.getMetadata();
        metadata.put("ip.internet", "http://__ip__:__port__/" + name + "/");
        metadata.put("ip.local", "http://" + ip + ":" + port + "/");
    }
}
