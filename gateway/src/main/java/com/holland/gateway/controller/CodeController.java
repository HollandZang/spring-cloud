package com.holland.gateway.controller;

import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import com.holland.common.spring.apis.gateway.ICodeController;
import com.holland.common.utils.Response;
import com.holland.common.utils.ValidateUtil;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.gateway.mapper.CodeTypeMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class CodeController implements ICodeController {

    @Resource
    private CodeMapper codeMapper;

    @Resource
    private CodeTypeMapper codeTypeMapper;

    @Override
    public Mono<Response<List<Map<String, String>>>> all(String type) {
        ValidateUtil.notEmpty(type, "类别代码");
        return Mono.defer(() -> Mono.just(Response.success(codeMapper.all(type))));
    }

    @Override
    public Mono<Response<Response<List<CodeType>>>> loginList(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return Mono.defer(() -> Mono.just(Response.success(Response.success(codeTypeMapper.list(pageHelper), codeTypeMapper.count()))));
    }

    @Override
    public Mono<Response<Integer>> add(@RequestBody Code code) {
        ValidateUtil.notEmpty(code.getType(), "类别代码");
        ValidateUtil.notEmpty(code.getCode(), "值");
        ValidateUtil.notEmpty(code.getName(), "名称");
        ValidateUtil.maxLength(code.getName(), 256, "名称");
        return Mono.defer(() -> Mono.just(Response.success(codeMapper.insert(code))));
    }
}
