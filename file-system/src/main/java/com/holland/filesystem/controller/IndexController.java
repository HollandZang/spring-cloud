package com.holland.filesystem.controller;

import com.holland.common.utils.Response;
import com.holland.filesystem.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;

@Controller
@RequestMapping()
public class IndexController {

    @Value("${spring.application.name}")
    private String name;

    @Resource
    private EmailService emailService;

    @GetMapping()
    public Mono<ResponseEntity<?>> test() {
        return Mono.defer(() -> Mono.just(name + "::OK\n" + new Date()))
                .map(it -> ResponseEntity.ok().body(it));
    }

    @GetMapping("feignTest")
    public Mono<ResponseEntity<?>> feignTest() {
        return emailService.test("asdzxcv")
                .doOnError(throwable -> System.out.println("doOnError"))
//                .onErrorResume(throwable -> Mono.just(Response.failed(throwable)))
                .onErrorReturn(Response.failed("调用 {} 服务失败", "emailService"))
//                .onErrorContinue(Response.failed("调用 {} 服务失败", "emailService"))
                .doOnNext(System.out::println)
                .doOnNext(stringResponse -> {
                    final int i = 1 / 0;
                })
                .doOnNext(System.out::println)
                .onErrorResume(throwable -> Mono.just(Response.failed(throwable)))
                .map(it -> ResponseEntity.ok().body(it));
    }
}
