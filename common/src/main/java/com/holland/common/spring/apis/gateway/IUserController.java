package com.holland.common.spring.apis.gateway;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.holland.common.aggregate.LoginUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Api(tags = "用户模块")
@RequestMapping("/user")
public interface IUserController {
    @ApiOperation("获取用户列表")
    @PostMapping("/list")
    Mono<Response<List<Map<String, Object>>>> list(@RequestBody Page<Map<String, Object>> page);

    @ApiOperation("登录")
    @PostMapping("/login")
    Mono<Response<LoginUser>> login(@RequestBody User user);

    /**
     * <a href="https://www.w3.org/TR/clear-site-data/">注销协议规范</a>
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
