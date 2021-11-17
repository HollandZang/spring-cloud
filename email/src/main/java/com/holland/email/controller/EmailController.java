package com.holland.email.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.Properties;

@Controller
@RequestMapping("email")
public class EmailController {

    @PostMapping("send")
    public Mono<ResponseEntity<?>> send(@RequestBody JSONObject o) {
        String to = o.getString("to");
        String subject = o.getString("subject");
        String text = o.getString("text");

        mail163(to, subject, text);
        return Mono.defer(() -> Mono.just("OK"))
                .map(it -> ResponseEntity.ok().body(it));
    }

    /**
     * 163邮箱发送消息
     */
    private void mail163(String to, String subject, String text) {
        String sender = "177811671532@163.com";

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(25);
        mailSender.setProtocol("smtp");
        mailSender.setUsername(sender);
        mailSender.setPassword("VXGSSMZKYHAYTRDD");
        mailSender.setJavaMailProperties(new Properties() {{
            setProperty("mail.debug", "true");
            setProperty("mail.smtp.auth", "true");
            setProperty("mail.smtp.starttls.enable", "true");
            setProperty("mail.smtp.starttls.required", "true");
        }});
        final SimpleMailMessage templateMessage = new SimpleMailMessage();
        templateMessage.setFrom(sender);
        templateMessage.setSubject(subject);

        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo(to);
        msg.setText(text);
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
