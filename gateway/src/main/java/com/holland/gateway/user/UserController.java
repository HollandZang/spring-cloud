package com.holland.gateway.user;

import com.holland.gateway.RedisUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

//    @Resource
//    private ServerHttpRequest request;

    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user) {
        final Optional<User> optional = userMapper.getByLoginName(user.getLoginName());
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.GONE).body("用户不存在");
        }
        if (encoder.matches(user.getPassword(), optional.get().getPassword())) {

            user.setPassword(null);
            redisUtil.setToken(user.getLoginName(), user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账号或密码错误");
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody User user) {
        final String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);

        final int row = userMapper.insert(user);
        //        encoder.matches("", encode);

//        if (routeWhitelist.getUrl() == null || routeWhitelist.getUrl().isEmpty()) {
//            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("url不能为空");
//        }
//        final Optional<RouteWhitelist> optional = routeWhitelistMapper.getByUrl(routeWhitelist.getUrl());
//        if (optional.isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("url已存在");
//        }
//        final int row = routeWhitelistMapper.insert(routeWhitelist);
        return ResponseEntity.ok(row);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("接受参数为空");
        }
        final int row = userMapper.update(user);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        return ResponseEntity.ok(row);
    }

}
