package com.holland.email.controller;

import com.holland.common.entity.email.MailSend;
import com.holland.common.spring.apis.hadoop.IEmailController;
import com.holland.common.utils.Response;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Properties;

@RestController
public class EmailController implements IEmailController {

    @Override
    public Mono<Response<String>> test(String str) {
        System.out.println(str);
        return Mono.defer(() -> Mono.just(Response.success(str)));
    }

    @Override
    public Mono<Response<?>> send(@RequestBody MailSend mailSend) {
        switch (mailSend.sender.host) {
            case NETEASE_163:
                mail163(mailSend);
                break;
            case GMAIL:
                break;
        }
        return Mono.defer(() -> Mono.just(Response.success()));
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
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

    /**
     * gmail邮箱发送消息
     * 暂不可用，可能是授权码的问题
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
        mailSender.setJavaMailProperties(new Properties() {{
            setProperty("mail.debug", "true");
            setProperty("mail.smtp.auth", "true");
            setProperty("mail.smtp.starttls.enable", "true");
            setProperty("mail.smtp.starttls.required", "true");
            setProperty("mail.smtp.quitwait", "false");
            setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            setProperty("mail.smtp.password", "wszhn001");
        }});
        final SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(sender);
        templateMessage.setSubject("Your order");

        String address = "17781671532@163.com";
        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo(address);
        msg.setText("test email");
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
