package com.holland.gateway.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.holland.common.aggregate.CacheUser;
import com.holland.common.aggregate.LoginUser;
import com.holland.common.entity.gateway.User;
import com.holland.common.entity.gateway.UserRole;
import com.holland.common.enums.gateway.RoleEnum;
import com.holland.common.plugin.mybaties.MyPageHelper;
import com.holland.common.spring.AuthCheck;
import com.holland.common.spring.apis.gateway.IUserController;
import com.holland.common.utils.Response;
import com.holland.common.utils.Validator;
import com.holland.gateway.common.RequestUtil;
import com.holland.gateway.common.UserCache;
import com.holland.gateway.mapper.UserMapper;
import com.holland.gateway.mapper.UserRoleMapper;
import com.holland.gateway.service.UserService;
import com.holland.redis.Lock;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class UserController implements IUserController {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserCache userCache;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @Override
    public Mono<Response<List<Map<String, Object>>>> listAndTotal(@RequestBody Page<Map<String, Object>> page) {
        final Page<Map<String, Object>> records = userMapper.selectMapsPage(page, new QueryWrapper<User>()
                .select("login_name", "create_time", "update_time"));
        return Mono.defer(() -> Mono.just(Response.success(records)));
    }

    @Override
    public Mono<Response<List<Map<String, Object>>>> list(Page<Map<String, Object>> page) {
        MyPageHelper.startPage(page);
        List<Map<String, Object>> users = userMapper.selectMaps(new QueryWrapper<User>()
                .select("login_name", "create_time", "update_time"));
        return Mono.defer(() -> Mono.just(Response.success(users)));
    }

    @Override
    public Mono<Response<Long>> total(Map<String, Object> param) {
        Long count = userMapper.selectCount(new QueryWrapper<User>());
        return Mono.defer(() -> Mono.just(Response.success(count)));
    }

    @Override
    public Mono<Response<LoginUser>> login(@RequestBody User user) {
        final String loginName = user.getLogin_name();
        final String password = user.getPassword();
        new Validator(user.getLogin_name(), "用户名").notEmpty().lenGE(8).lenLT(16);
        new Validator(user.getPassword(), "密码").notEmpty().lenLT(16);

        return Mono.defer(() -> {
            try (Lock lock = userCache.lock("login", user.getLogin_name())) {
                if (!lock.isLocked())
                    return Mono.just(Response.later());
                final User dbUser = userMapper.getByLogin_name(loginName);
                if (dbUser == null) {
//                return Mono.just(Response.failed("用户不存在"));
                    return Mono.just(Response.failed("账号或密码错误"));
                }
                if (encoder.matches(password, dbUser.getPassword())) {
                    userCache.delByLoginName(dbUser.getLogin_name());

                    final UserRole userRole = userRoleMapper.getByLogin_name(loginName);
                    if (userRole != null && userRole.getRoles() != null)
                        dbUser.setRoles(userRole.getRoles());

                    final LoginUser vo = LoginUser.from(dbUser);
                    vo.setPassword(null);
                    vo.setToken(userCache.cache(dbUser));
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
        new Validator(user.getLogin_name(), "用户名").notEmpty().lenGE(8).lenLT(16);
        new Validator(user.getPassword(), "密码").notEmpty().lenLT(16);

        return Mono.defer(() -> {
            final User dbUser = userMapper.getByLogin_name(user.getLogin_name());
            if (dbUser != null)
                return Mono.just(Response.existErr("账号"));

            final String encode = encoder.encode(user.getPassword());
            final Date now = new Date();
            final int row = userMapper.insert(
                    user.setPassword(encode)
                            .setCreate_time(now)
                            .setUpdate_time(now));
            return Mono.just(Response.success(row));
        });
    }

    @AuthCheck(values = RoleEnum.TOKEN)
    @Override
    public Mono<Response<Integer>> update(ServerHttpRequest request, @RequestBody User user) {
        new Validator(user.getPassword(), "密码").lenLT(16);
        new Validator(user.getRoles(), "角色").lenLT(256);

        final CacheUser cacheUser = RequestUtil.getCacheUser(request);
        if (cacheUser == null)
            return Mono.defer(() -> Mono.just(Response.notExistErr("资源")));
        final String login_name = cacheUser.getLogin_name();
        user.setLogin_name(login_name);

        try (Lock lock = userCache.lock("update", login_name)) {
            if (!lock.isLocked())
                return Mono.just(Response.later());

            userService.updateUser(request, user, cacheUser, login_name, encoder);
            return Mono.defer(() -> Mono.just(Response.success()));
        }
    }
}
