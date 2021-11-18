package com.holland.common.entity.email;

public class MailSend {
    public final MailSender sender;
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
