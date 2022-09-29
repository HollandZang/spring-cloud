package com.holland.gateway.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import com.holland.common.spring.apis.gateway.ICodeController;
import com.holland.common.utils.Response;
import com.holland.common.utils.Validator;
import com.holland.gateway.common.CommCache;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.gateway.mapper.CodeTypeMapper;
import com.holland.redis.Lock;
import com.holland.redis.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CodeController extends CommCache implements ICodeController {

    @Resource
    private CodeMapper codeMapper;

    @Resource
    private CodeTypeMapper codeTypeMapper;

    public CodeController(@Autowired Redis redis) {
        super(redis);
    }

    @Override
    public Mono<Response<List<CodeType>>> typeAll() {
        return Mono.defer(() -> {
            final List<CodeType> codeTypes = r_getOrReassign(() -> codeTypeMapper.selectList(null)
                    , "codeType");
            return Mono.just(Response.success(codeTypes));
        });
    }

    @Override
    public Mono<Response<Integer>> typeAdd(ServerHttpRequest request, @RequestBody CodeType codeType) {
        new Validator(codeType.getId(), "ID").notEmpty().lenLT(4);
        new Validator(codeType.getDes(), "描述").notEmpty().lenLT(256);
        return Mono.defer(() -> {
            try (final Lock lock = redis.lock(CodeType.class, codeType.getId())) {
                if (!lock.isLocked())
                    return Mono.just(Response.later());

                if (codeTypeMapper.selectById(codeType.getId()) != null)
                    return Mono.just(Response.existErr(codeType.getId()));

                final int insert = codeTypeMapper.insert(codeType);
                r_remove("codeType");
                return Mono.just(Response.success(insert));
            }
        });
    }

    @Override
    public Mono<Response<List<Code>>> all(@PathVariable String type) {
        new Validator(type, "类别代码").notEmpty();
        return Mono.defer(() -> {
            final List<Code> codes = r_getOrReassign(() -> codeMapper.getByCode_type_id(type)
                    , "code", type);
            return Mono.just(Response.success(codes));
        });
    }

    @Override
    public Mono<Response<Integer>> add(ServerHttpRequest request, @RequestBody Code code) {
        new Validator(code.getVal(), "值").notEmpty().lenLT(256);
        new Validator(code.getVal(), "值1").notEmpty().lenLT(1024);
        new Validator(code.getCode_type_id(), "类别代码").notEmpty().lenLT(4);
        new Validator(code.getDes(), "描述").notEmpty().lenLT(256);
        return Mono.defer(() -> {
            try (final Lock lock = redis.lock(Code.class, code.getCode_type_id())) {
                if (!lock.isLocked())
                    return Mono.just(Response.later());
                final Code find = codeMapper.selectOne(new QueryWrapper<Code>()
                        .eq("code_type_id", code.getCode_type_id())
                        .eq("val", code.getVal())
                        .eq("des", code.getDes())
                );
                if (find == null) {
                    codeMapper.insert(code);
                    r_remove("code", code.getCode_type_id());
                    return Mono.just(Response.success(code.getId()));
                } else {
                    return Mono.just(Response.existErr("{}[{}]", code.getDes(), code.getVal()));
                }
            }
        });
    }
}
