package com.holland.gateway.controller;

import com.holland.gateway.sqlHelper.PageHelper;
import com.holland.gateway.mapper.LogLoginMapper;
import com.holland.gateway.mapper.LogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "日志模块")
@Controller
@RequestMapping("/log")
public class LogController {

    @Resource
    private LogMapper logMapper;

    @Resource
    private LogLoginMapper logLoginMapper;

    @ApiOperation("获取操作日志")
    @GetMapping("/list")
    public ResponseEntity<?> list(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return ResponseEntity.ok(Map.of("data", logMapper.list(pageHelper), "count", logMapper.count()));
    }

    @ApiOperation("获取登录日志")
    @GetMapping("/login/list")
    public ResponseEntity<?> loginList(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return ResponseEntity.ok(Map.of("data", logLoginMapper.list(pageHelper), "count", logLoginMapper.count()));
    }
}
