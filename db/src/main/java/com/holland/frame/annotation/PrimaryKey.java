package com.holland.frame.annotation;

import com.holland.frame.IncrementStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    IncrementStrategy autoIncrement() default IncrementStrategy.AUTO;

    String indexType() default "";

    String indexMethod() default "";
}
