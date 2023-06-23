package com.holland.common.entity.email;

import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Valid
public class MailSend {
    public final MailSender sender;
    @NotEmpty(message = "to is empty!")
    public final String to;
    public final String subject;
    public final String text;

    public MailSend(MailSender sender, String to, String subject, String text) {
        this.sender = sender;
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
