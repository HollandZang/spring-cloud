package com.holland.gateway.conf;

import com.holland.common.spring.apis.email.IEmailController;
import reactivefeign.spring.config.ReactiveFeignClient;

@ReactiveFeignClient(value = "email")
public interface EmailService extends IEmailController {

}
