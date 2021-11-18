package com.holland.common.spring.apis.email;

import com.holland.common.entity.email.MailSend;
import com.holland.common.utils.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequestMapping("/core")
public interface IEmailController {

    @GetMapping("/test")
    Mono<Response<String>> test(String str);

    @PostMapping("/send")
    Mono<Response<?>> send(@RequestBody MailSend mailSend);
}
