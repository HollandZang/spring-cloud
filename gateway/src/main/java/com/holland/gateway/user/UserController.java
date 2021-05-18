package com.holland.gateway.user;

import com.alibaba.fastjson.JSONObject;
import com.holland.gateway.common.RedisUtil;
import com.holland.gateway.common.ValidateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JSONObject o) {
        final String loginName = o.getString("loginName");
        final String password = o.getString("password");
        final String software = o.getString("software");    /*指明通过什么软件、项目登录*/
        ValidateUtil.validateNotEmpty(loginName, "用户名");
        ValidateUtil.validateNotEmpty(password, "密码");
        ValidateUtil.validateNotEmpty(software, "software");

        final Optional<User> optional = userMapper.getByLoginName(loginName);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.GONE).body("用户不存在");
        }
        final User dbUser = optional.get();
        if (encoder.matches(password, dbUser.getPassword())) {

            dbUser.setPassword(null);
            redisUtil.setToken(loginName, dbUser);
            return ResponseEntity.ok(dbUser);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账号或密码错误");
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody User user) {
        ValidateUtil.validateNotEmpty(user.getLoginName(), "用户名");
        ValidateUtil.validateLength(user.getLoginName(), 16, "用户名");
        ValidateUtil.validateNotEmpty(user.getPassword(), "密码");
        ValidateUtil.validateLength(user.getPassword(), 16, "密码");

        final String encode = encoder.encode(user.getPassword());
        final LocalDateTime now = LocalDateTime.now();
        final int row = userMapper.insert(
                user.setPassword(encode)
                        .setCreateTime(now)
                        .setUpdateTime(now));
        return ResponseEntity.ok(row);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User user) {
        ValidateUtil.validateLength(user.getLoginName(), 16, "用户名");
        ValidateUtil.validateLength(user.getPassword(), 16, "密码");

        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        final int row = userMapper.update(
                user.setUpdateTime(LocalDateTime.now()));
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        return ResponseEntity.ok(row);
    }

}
