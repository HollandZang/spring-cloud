package com.holland.gateway.controller;

import com.holland.gateway.mapper.LogLoginMapper;
import com.holland.gateway.mapper.LogMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogMapper logMapper;

    @Resource
    private LogLoginMapper logLoginMapper;

    @GetMapping("/list")
    public ResponseEntity<?> list(Integer page, Integer limit) {
        final int offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * limit;
        limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        return ResponseEntity.ok(Map.of("data", logMapper.list(offset, limit), "count", logMapper.count()));
    }

    @GetMapping("/login/list")
    public ResponseEntity<?> loginList(Integer page, Integer limit) {
        final int offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * limit;
        limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        return ResponseEntity.ok(Map.of("data", logLoginMapper.list(offset, limit), "count", logLoginMapper.count()));
    }
}
