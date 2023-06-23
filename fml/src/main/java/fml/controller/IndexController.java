package fml.controller;

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

    @GetMapping("/index")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("hello index");
    }

    @GetMapping("/indexAuth")
    public ResponseEntity<?> indexAuth() {
        return ResponseEntity.ok().body("hello indexAuth");
    }
}
