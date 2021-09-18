package com.holland.filesystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Date;

@Controller
@RequestMapping()
public class IndexController {

    @GetMapping()
    public Mono<ResponseEntity<?>> test() {
        return Mono.defer(() -> Mono.just("file_system::OK\n" + new Date()))
                .map(it -> ResponseEntity.ok().body(it));
    }

    @GetMapping("t")
    public ResponseEntity<?> t() {
        return ResponseEntity.ok().body("file_system::OK\n" + new Date());
    }
}
