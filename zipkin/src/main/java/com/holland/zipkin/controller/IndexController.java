package com.holland.zipkin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Date;

@Controller
@RequestMapping()
public class IndexController {
    @Value("${spring.application.name}")
    private String name;

    @GetMapping()
    public Mono<ResponseEntity<?>> test() {
        return Mono.defer(() -> Mono.just(name + "::OK\n" + new Date()))
                .map(it -> ResponseEntity.ok().body(it));
    }
}
