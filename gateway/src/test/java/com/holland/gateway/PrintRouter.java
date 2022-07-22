package com.holland.gateway;

import com.holland.common.spring.AuthCheckMapping;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PrintRouter {
    @Resource
    private AuthCheckMapping authCheckMapping;

    @Test
    void contextLoads() {
        authCheckMapping.forEach((k, v) -> {
            final String[] s = k.split(" ");
            System.err.printf("%s\\u0020%s=%s\n", s[0], s[1], v == null ? "" : v);
        });
    }
}
