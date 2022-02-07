package com.holland.gateway.controller;

import com.holland.common.aggregate.LoginUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.spring.apis.gateway.IUserController;
import com.holland.common.utils.Response;
import com.holland.common.utils.Validator;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.common.RedisController;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.mapper.UserMapper;
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
    private RedisController redisController;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @Override
    public Mono<Response<List<User>>> list(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return Mono.defer(() -> Mono.just(Response.success(userMapper.list(pageHelper), userMapper.count())));
    }

    @Override
    public Mono<Response<LoginUser>> login(@RequestBody User user) {
        final String loginName = user.getLoginName();
        final String password = user.getPassword();
        Validator.test(loginName, "用户名").notEmpty();
        Validator.test(password, "密码").notEmpty();

        return Mono.defer(() -> {
            final Optional<User> optional = userMapper.selectByLoginName(loginName);
            if (optional.isEmpty()) {
//                return Mono.just(Response.failed("用户不存在"));
                return Mono.just(Response.failed("账号或密码错误"));
            }
            final User dbUser = optional.get();
            if (encoder.matches(password, dbUser.getPassword())) {
                final LoginUser vo = LoginUser.from(dbUser);
                vo.setPassword(null);
                vo.setToken(redisController.setToken(loginName, vo));
                return Mono.just(Response.success(vo));
            } else {
                return Mono.just(Response.failed("账号或密码错误"));
            }
        });
    }

    @Override
    public Mono<Response<Boolean>> logout(ServerHttpRequest request, ServerHttpResponse response) {
        final String token = RequestUtil.getToken(request);
        final Boolean aBoolean = redisController.delToken(token);

        return Mono.defer(() -> {
            response.getHeaders().add("Clear-Site-Data", "\"cache\", \"cookies\", \"storage\", \"executionContexts\"");
            return Mono.just(Response.success(aBoolean));
        });
    }

    @Override
    public Mono<Response<Integer>> add(@RequestBody User user) {
        Validator.test(user.getLoginName(), "用户名").notEmpty().maxLength(16);
        Validator.test(user.getPassword(), "密码").notEmpty().maxLength(16);

        return Mono.defer(() -> {
            final Optional<User> optional = userMapper.selectByLoginName(user.getLoginName());
            if (optional.isPresent()) {
                return Mono.just(Response.failed("账号已存在"));
            }

            final String encode = encoder.encode(user.getPassword());
            final Date now = new Date();
            final int row = userMapper.insertSelective(
                    user.setPassword(encode)
                            .setCreateTime(now)
                            .setUpdateTime(now));
            return Mono.just(Response.success(row));
        });
    }

    @Override
    public Mono<Response<Integer>> update(ServerHttpRequest request, @RequestBody User user) {
        Validator.test(user.getPassword(), "密码").maxLength(16);

        user.setLoginName(RequestUtil.getLoginName(request));
        final Optional<User> optional = userMapper.selectByLoginName(user.getLoginName());
        if (optional.isEmpty()) {
            return Mono.defer(() -> Mono.just(Response.failed("资源不存在")));
        }
        // TODO: 2022/2/7 旧密码匹配尝试

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
