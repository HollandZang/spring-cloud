package com.holland.common.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    AuthCheckEnum[] values();

    enum AuthCheckEnum {
        TOKEN, ADMIN;
    }

}
