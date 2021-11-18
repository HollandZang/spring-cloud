package com.holland.common.spring.apis.gateway;

import com.holland.common.entity.gateway.Log;
import com.holland.common.entity.gateway.LogLogin;
import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;

@Api(tags = "日志模块")
@RequestMapping("/log")
public interface ILogController {
    @ApiOperation("获取操作日志")
    @GetMapping("/list")
    Mono<Response<List<Log>>> list(Integer page, Integer limit);

    @ApiOperation("获取登录日志")
    @GetMapping("/login/list")
    Mono<Response<List<LogLogin>>> loginList(Integer page, Integer limit);
}
