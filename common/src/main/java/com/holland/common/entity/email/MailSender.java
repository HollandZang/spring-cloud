package com.holland.common.entity.email;

import com.holland.common.enums.email.MailHost;
import com.holland.common.enums.email.MailProtocol;

public class MailSender {
    public final String sendAddress;
    public final MailHost host;
    public final int port;
    public final MailProtocol protocol;
    public final String password;

    public MailSender(String sendAddress, MailHost host, int port, MailProtocol protocol, String password) {
        this.sendAddress = sendAddress;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.password = password;
    }
}
