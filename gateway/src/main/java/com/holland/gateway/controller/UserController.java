package com.holland.gateway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.holland.common.aggregate.LoginUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.spring.apis.gateway.IUserController;
import com.holland.common.utils.Response;
import com.holland.common.utils.Validator;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.gateway.mapper.UserMapper;
import com.holland.redis.Lock;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
    private UserCache userCache;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @Override
    public Mono<Response<List<User>>> list(Page<User> page) {
        final Page<User> userPage = userMapper.selectPage(page, null);
        for (User user : userPage.getRecords()) {
            user.setPassword(null);
        }
        return Mono.defer(() -> Mono.just(Response.success(userPage)));
    }

    @Override
    public Mono<Response<LoginUser>> login(@RequestBody User user) {
        final String loginName = user.getLogin_name();
        final String password = user.getPassword();
        Validator.test(user.getLogin_name(), "用户名").notEmpty().minLength(8).maxLength(16);
        Validator.test(user.getPassword(), "密码").notEmpty().maxLength(16);

        return Mono.defer(() -> {
            try (Lock lock = userCache.lock("login", user.getLogin_name())) {
                if (!lock.isLocked())
                    return Mono.just(Response.later());
                final Optional<User> optional = userMapper.selectByLoginName(loginName);
                if (optional.isEmpty()) {
//                return Mono.just(Response.failed("用户不存在"));
                    return Mono.just(Response.failed("账号或密码错误"));
                }
                final User dbUser = optional.get();
                if (encoder.matches(password, dbUser.getPassword())) {
                    userCache.delByLoginName(dbUser.getLogin_name());

                    final LoginUser vo = LoginUser.from(dbUser);
                    vo.setPassword(null);
                    vo.setToken(userCache.cache(user));
                    return Mono.just(Response.success(vo));
                } else {
                    return Mono.just(Response.failed("账号或密码错误"));
                }
            }
        });
    }

    @Override
    public Mono<Response<Boolean>> logout(ServerHttpRequest request, ServerHttpResponse response) {
        final String token = RequestUtil.getToken(request);
        final Boolean aBoolean = userCache.del(token);

        return Mono.defer(() -> {
            response.getHeaders().add("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\", \"executionContexts\"");
            return Mono.just(Response.success(aBoolean));
        });
    }

    @Override
    public Mono<Response<Integer>> add(@RequestBody User user) {
        Validator.test(user.getLogin_name(), "用户名").notEmpty().minLength(8).maxLength(16);
        Validator.test(user.getPassword(), "密码").notEmpty().maxLength(16);

        return Mono.defer(() -> {
            final Optional<User> optional = userMapper.selectByLoginName(user.getLogin_name());
            if (optional.isPresent()) {
                return Mono.just(Response.failed("账号已存在"));
            }

            final String encode = encoder.encode(user.getPassword());
            final Date now = new Date();
            final int row = userMapper.insert(
                    user.setPassword(encode)
                            .setCreate_time(now)
                            .setUpdate_time(now));
            return Mono.just(Response.success(row));
        });
    }

    @Override
    public Mono<Response<Integer>> update(ServerHttpRequest request, @RequestBody User user) {
        Validator.test(user.getPassword(), "密码").maxLength(16);

        user.setLogin_name(RequestUtil.getCacheUser(request).getLogin_name());

        try (Lock lock = userCache.lock("update", user.getLogin_name())) {
            if (!lock.isLocked())
                return Mono.just(Response.later());
            final Optional<User> optional = userMapper.selectByLoginName(user.getLogin_name());
            if (optional.isEmpty()) {
                return Mono.defer(() -> Mono.just(Response.failed("资源不存在")));
            }

            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(encoder.encode(user.getPassword()));
            }

            final int row = userMapper.updateByUserSelective(
                    user.setUpdate_time(new Date()));
            if (row == 0) {
                return Mono.defer(() -> Mono.just(Response.failed("资源不存在")));
            }
            return Mono.defer(() -> Mono.just(Response.success(row)));
        }
    }

}
