package com.holland.common.spring.apis.hadoop;

import com.holland.common.entity.email.MailSend;
import com.holland.common.utils.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@RequestMapping("/core")
public interface IEmailController {

    @PostMapping("/test")
    Mono<Response<String>> test(@RequestParam String str);

    @PostMapping("/send")
    Mono<Response<?>> send(@RequestBody MailSend mailSend);
}
