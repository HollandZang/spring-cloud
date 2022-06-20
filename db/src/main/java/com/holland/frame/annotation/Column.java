package com.holland.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String dbType();

    long length() default 0L;

    int scale() default 0;

    String defaultVal() default "";

    String comment() default "";

    boolean notNull() default false;
}
