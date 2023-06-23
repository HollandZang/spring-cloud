package com.holland.email.controller;

import com.holland.common.entity.email.MailSend;
import com.holland.common.spring.apis.email.IEmailController;
import com.holland.common.utils.Response;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Properties;

@RestController
public class EmailController implements IEmailController {

    @Override
    public Mono<Response<String>> test(@RequestBody String str) {
        System.out.println(str);
        return Mono.defer(() -> Mono.just(Response.success(str)));
    }

    @Override
    public Mono<Response<?>> send(@Valid @RequestBody MailSend mailSend) {
        return Mono.defer(() -> {
            switch (mailSend.sender.host) {
                case NETEASE_163:
                    mail163(mailSend);
                    break;
                case GMAIL:
                    break;
                default:
                    return Mono.just(Response.failed("无效的host：" + mailSend.sender.host));
            }
            return Mono.just(Response.success());
        });
    }

    /**
     * 163邮箱发送消息
     */
    private void mail163(MailSend mailSend) {
        String sender = mailSend.sender.sendAddress;

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSend.sender.host.value);
        mailSender.setPort(mailSend.sender.port);
        mailSender.setProtocol(mailSend.sender.protocol.value);
        mailSender.setUsername(sender);
        mailSender.setPassword(mailSend.sender.password);
        mailSender.setJavaMailProperties(mailSend.sender.host.getProperties());
        final SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(sender);
        templateMessage.setSubject(mailSend.subject);

        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo(mailSend.to);
        msg.setText(mailSend.text);

        mailSender.send(msg);
    }

    /**
     * gmail邮箱发送消息
     * todo 暂不可用，可能是授权码的问题
     */
    private void gmail() {
        String sender = "zhn.pop@gmail.com";
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
//        mailSender.setPort(465);
        mailSender.setProtocol("smtp");
        mailSender.setUsername(sender);
        mailSender.setPassword("");
        final Properties properties = new Properties();
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.quitwait", "false");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.password", "");
        mailSender.setJavaMailProperties(properties);
        final SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(sender);
        templateMessage.setSubject("Your order");

        String address = "17781671532@163.com";
        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo(address);
        msg.setText("test email");

        mailSender.send(msg);
    }
}
