package com.holland.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.holland.common.utils.ValidateUtil;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.common.RedisController;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.domain.User;
import com.holland.gateway.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Api(tags = "用户模块")
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisController redisController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public ResponseEntity<?> list(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return ResponseEntity.ok(Map.of("data", userMapper.list(pageHelper), "count", userMapper.count()));
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JSONObject o) {
        final String loginName = o.getString("loginName");
        final String password = o.getString("password");
        ValidateUtil.notEmpty(loginName, "用户名");
        ValidateUtil.notEmpty(password, "密码");

        final Optional<User> optional = userMapper.selectByLoginName(loginName);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.GONE).body("用户不存在");
        }
        final User dbUser = optional.get();
        if (encoder.matches(password, dbUser.getPassword())) {
            dbUser.setPassword(null);
            final String token = redisController.setToken(loginName, dbUser);
            final JSONObject json = (JSONObject) JSONObject.toJSON(dbUser);
            json.put("token", token);
            return ResponseEntity.ok(json);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账号或密码错误");
        }
    }

    /**
     * https://www.w3.org/TR/clear-site-data/
     */
    @ApiOperation("注销")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(ServerHttpRequest request) {
        final String token = RequestUtil.getToken(request);
        final Boolean aBoolean = redisController.delToken(token);
        return ResponseEntity.ok()
                .header("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\", \"executionContexts\"")
                .body(aBoolean);
    }

    @ApiOperation("新增用户")
    @PostMapping("/create")
    public ResponseEntity<?> add(@RequestBody User user) {
        ValidateUtil.notEmpty(user.getLoginName(), "用户名");
        ValidateUtil.maxLength(user.getLoginName(), 16, "用户名");
        ValidateUtil.notEmpty(user.getPassword(), "密码");
        ValidateUtil.maxLength(user.getPassword(), 16, "密码");

        final Optional<User> optional = userMapper.selectByLoginName(user.getLoginName());
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("账号已存在");
        }

        final String encode = encoder.encode(user.getPassword());
        final Date now = new Date();
        final int row = userMapper.insertSelective(
                user.setPassword(encode)
                        .setCreateTime(now)
                        .setUpdateTime(now));
        return ResponseEntity.ok(row);
    }

    @ApiOperation("更新用户信息")
    @PutMapping
    public ResponseEntity<?> update(ServerHttpRequest request, @RequestBody User user) {
        user.setLoginName(RequestUtil.getLoginName(request));
        ValidateUtil.maxLength(user.getPassword(), 16, "密码");

        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        final int row = userMapper.updateByUserSelective(
                user.setUpdateTime(new Date()));
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        return ResponseEntity.ok(row);
    }

}
