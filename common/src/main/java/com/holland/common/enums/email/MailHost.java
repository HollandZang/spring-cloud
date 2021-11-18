package com.holland.common.enums.email;

import java.util.Properties;

public enum MailHost {
    NETEASE_163("smtp.163.com") {
        @Override
        public Properties getProperties() {
            final Properties properties = new Properties();
            properties.setProperty("mail.debug", "true");
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.starttls.required", "true");
            return properties;
        }
    },
    GMAIL("smtp.gmail.com") {
        @Override
        public Properties getProperties() {
            return null;
        }
    },
    ;

    public final String value;

    MailHost(String value) {
        this.value = value;
    }

    public abstract Properties getProperties();
}
