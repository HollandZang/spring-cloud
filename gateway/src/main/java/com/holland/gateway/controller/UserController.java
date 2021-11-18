package com.holland.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.holland.common.entity.gateway.User;
import com.holland.common.spring.apis.gateway.IUserController;
import com.holland.common.utils.Response;
import com.holland.common.utils.ValidateUtil;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.common.RedisController;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController implements IUserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisController redisController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @Override
    public Mono<Response<List<User>>> list(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return Mono.defer(() -> Mono.just(Response.success(userMapper.list(pageHelper), userMapper.count())));
    }

    @Override
    public Mono<Response<?>> login(@RequestBody User user) {
        final String loginName = user.getLoginName();
        final String password = user.getPassword();
        ValidateUtil.notEmpty(loginName, "用户名");
        ValidateUtil.notEmpty(password, "密码");

        final Optional<User> optional = userMapper.selectByLoginName(loginName);
        if (optional.isEmpty()) {
            return Mono.defer(() -> Mono.just(Response.failed("用户不存在")));
        }
        final User dbUser = optional.get();
        if (encoder.matches(password, dbUser.getPassword())) {
            dbUser.setPassword(null);
            final String token = redisController.setToken(loginName, dbUser);
            final JSONObject json = (JSONObject) JSONObject.toJSON(dbUser);
            json.put("token", token);
            return Mono.defer(() -> Mono.just(Response.success(json)));
        } else {
            return Mono.defer(() -> Mono.just(Response.failed("账号或密码错误")));
        }
    }

    @Override
    public ResponseEntity<?> logout(ServerHttpRequest request) {
        final String token = RequestUtil.getToken(request);
        final Boolean aBoolean = redisController.delToken(token);
        return ResponseEntity.ok()
                .header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\", \"executionContexts\"")
                .body(aBoolean);
    }

    @Override
    public Mono<Response<?>> add(@RequestBody User user) {
        ValidateUtil.notEmpty(user.getLoginName(), "用户名");
        ValidateUtil.maxLength(user.getLoginName(), 16, "用户名");
        ValidateUtil.notEmpty(user.getPassword(), "密码");
        ValidateUtil.maxLength(user.getPassword(), 16, "密码");

        final Optional<User> optional = userMapper.selectByLoginName(user.getLoginName());
        if (optional.isPresent()) {
            return Mono.defer(() -> Mono.just(Response.failed("账号已存在")));
        }

        final String encode = encoder.encode(user.getPassword());
        final Date now = new Date();
        final int row = userMapper.insertSelective(
                user.setPassword(encode)
                        .setCreateTime(now)
                        .setUpdateTime(now));
        return Mono.defer(() -> Mono.just(Response.success(row)));
    }

    @Override
    public Mono<Response<?>> update(ServerHttpRequest request, @RequestBody User user) {
        user.setLoginName(RequestUtil.getLoginName(request));
        ValidateUtil.maxLength(user.getPassword(), 16, "密码");

        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        final int row = userMapper.updateByUserSelective(
                user.setUpdateTime(new Date()));
        if (row == 0) {
            return Mono.defer(() -> Mono.just(Response.failed("资源不存在")));
        }
        return Mono.defer(() -> Mono.just(Response.success(row)));
    }

}
