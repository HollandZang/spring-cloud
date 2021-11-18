package com.holland.gateway.controller;

import com.holland.common.entity.gateway.Log;
import com.holland.common.entity.gateway.LogLogin;
import com.holland.common.spring.apis.gateway.ILogController;
import com.holland.common.utils.Response;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.mapper.LogLoginMapper;
import com.holland.gateway.mapper.LogMapper;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LogController implements ILogController {

    @Resource
    private LogMapper logMapper;

    @Resource
    private LogLoginMapper logLoginMapper;

    @Override
    public Mono<Response<List<Log>>> list(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return Mono.defer(() -> Mono.just(Response.success(logMapper.list(pageHelper), logMapper.count())));
    }

    @Override
    public Mono<Response<List<LogLogin>>> loginList(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return Mono.defer(() -> Mono.just(Response.success(logLoginMapper.list(pageHelper), logLoginMapper.count())));
    }
}
