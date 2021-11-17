package com.holland.common.spring.configuration;

import com.holland.common.utils.Response;
import com.holland.common.utils.ValidateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@RestControllerAdvice
public class GlobalExceptionHandle {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    public Response<?> handle(Exception e) {
        logger.error("服务器异常", e);
        return Response.failed(e);
    }

    @ExceptionHandler(value = {BindException.class, ValidateUtil.ParameterException.class})
    public Response<?> handleParameter(Exception e) {
        return Response.failed("参数解析异常: " + e.getMessage());
    }
}

