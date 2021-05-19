package com.holland.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.holland.gateway.common.RedisController;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.ValidateUtil;
import com.holland.gateway.domain.User;
import com.holland.gateway.mapper.UserMapper;
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

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisController redisController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @GetMapping("/list")
    public ResponseEntity<?> list(Integer page, Integer limit) {
        final int offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * limit;
        limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        return ResponseEntity.ok(Map.of("data", userMapper.list(offset, limit), "count", userMapper.count()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JSONObject o) {
        final String loginName = o.getString("loginName");
        final String password = o.getString("password");
        final String from = o.getString("from");    /*指明通过什么软件、项目登录*/
        ValidateUtil.notEmpty(loginName, "用户名");
        ValidateUtil.notEmpty(password, "密码");
        ValidateUtil.notEmpty(from, "from");

        final Optional<User> optional = userMapper.selectByLoginName(loginName);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.GONE).body("用户不存在");
        }
        final User dbUser = optional.get();
        if (encoder.matches(password, dbUser.getPassword())) {

            dbUser.setPassword(null);
            redisController.setToken(loginName, dbUser);
            return ResponseEntity.ok(dbUser);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账号或密码错误");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(ServerHttpRequest request) {
        final String token = RequestUtil.getToken(request);
        redisController.delToken(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody User user) {
        ValidateUtil.notEmpty(user.getLoginName(), "用户名");
        ValidateUtil.maxLength(user.getLoginName(), 16, "用户名");
        ValidateUtil.notEmpty(user.getPassword(), "密码");
        ValidateUtil.maxLength(user.getPassword(), 16, "密码");

        final String encode = encoder.encode(user.getPassword());
        final Date now = new Date();
        final int row = userMapper.insertSelective(
                user.setPassword(encode)
                        .setCreateTime(now)
                        .setUpdateTime(now));
        return ResponseEntity.ok(row);
    }

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
