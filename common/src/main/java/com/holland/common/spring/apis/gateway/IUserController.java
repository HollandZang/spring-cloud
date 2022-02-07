package com.holland.common.spring.apis.gateway;

import com.holland.common.aggregate.LoginUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Api(tags = "用户模块")
@RequestMapping("/user")
public interface IUserController {
    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    Mono<Response<List<User>>> list(Integer page, Integer limit);

    @ApiOperation("登录")
    @PostMapping("/login")
    Mono<Response<LoginUser>> login(@RequestBody User user);

    /**
     * https://www.w3.org/TR/clear-site-data/
     */
    @ApiOperation("注销")
    @PostMapping("/logout")
    Mono<Response<Boolean>> logout(ServerHttpRequest request, ServerHttpResponse response);

    @ApiOperation("新增用户")
    @PostMapping("/create")
    Mono<Response<Integer>> add(@RequestBody User user);

    @ApiOperation("更新用户信息")
    @PutMapping
    Mono<Response<Integer>> update(ServerHttpRequest request, @RequestBody User user);
}
