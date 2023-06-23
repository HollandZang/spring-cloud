package com.holland.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Response;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.conf.EmailService;
import com.holland.gateway.mapper.MiniappMapper;
import com.holland.gateway.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping()
public class IndexController {
    @Value("${spring.application.name}")
    private String name;

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private WebClient.Builder webClient;
//    @Resource
//    private EmailService emailService;
    @Resource
    private MiniappMapper miniappMapper;

    @GetMapping()
    public Mono<ResponseEntity<?>> test(ServerHttpRequest req) {
        Miniapp wx27abd25e951e1c5c = miniappMapper.selectById("wx27abd25e951e1c5c");

//        final String forObject = restTemplate.getForObject("http://gateway/call", String.class);
//        System.out.println("restTemplate call::" + forObject);

//        final String block = webClient.build()
//                .get()
//                .uri("http://gateway/call")
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//        System.out.println("webClient call::" + block);

//        final Response<String> response = emailService.test("asdzxcv").block();
//        System.out.println("feign call::" + response);

//        System.out.println(RequestUtil.getToken(req));
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return Mono.defer(() -> Mono.just(JSON.toJSONString(wx27abd25e951e1c5c)))
                .map(it -> ResponseEntity.ok().body(it));
    }

    @GetMapping("call")
    public Mono<ResponseEntity<?>> call() {
        return Mono.defer(() -> Mono.just("invoke ok"))
                .map(it -> ResponseEntity.ok().body(it));
    }
}
