package com.holland.gateway.conf;

import com.holland.common.configuration.GlobalExceptionHandle;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandle.class)
@Configuration
public class HollandConf {
}
