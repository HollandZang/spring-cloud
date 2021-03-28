package com.holland.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<?> handle(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getClass().getName() + ":\t" + e.getMessage());
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseEntity<?> handle(BindException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(e.getClass().getName() + ":\t" + e.getMessage());
    }
}

