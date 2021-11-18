package com.holland.filesystem.controller;

import com.holland.common.spring.apis.hadoop.IEmailController;
import reactivefeign.spring.config.ReactiveFeignClient;

@ReactiveFeignClient(value = "email")
public interface EmailService extends IEmailController {

}