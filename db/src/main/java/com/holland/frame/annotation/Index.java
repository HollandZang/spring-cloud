package com.holland.frame.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Indexes.class)
public @interface Index {
    String indexName();

    String indexType() default "";

    String indexMethod() default "";
}
