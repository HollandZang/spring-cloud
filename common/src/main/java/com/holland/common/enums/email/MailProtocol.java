package com.holland.common.enums.email;

public enum MailProtocol {
    SMTP("smtp"),
    ;

    public final String value;

    MailProtocol(String value) {
        this.value = value;
    }
}
