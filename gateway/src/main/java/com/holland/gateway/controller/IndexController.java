package com.holland.gateway.controller;

import com.holland.common.utils.Response;
import com.holland.gateway.conf.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    @GetMapping()
    public Mono<ResponseEntity<?>> test() {
        final String forObject = restTemplate.getForObject("http://gateway/call", String.class);
        System.out.println("restTemplate call::" + forObject);

        final String block = webClient.build()
                .get()
                .uri("http://gateway/call")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("webClient call::" + block);

//        final Response<String> response = emailService.test("asdzxcv").block();
//        System.out.println("feign call::" + response);

        return Mono.defer(() -> Mono.just(name + "::OK\n" + new Date()))
                .map(it -> ResponseEntity.ok().body(it));
    }

    @GetMapping("call")
    public Mono<ResponseEntity<?>> call() {
        return Mono.defer(() -> Mono.just("invoke ok"))
                .map(it -> ResponseEntity.ok().body(it));
    }
}
