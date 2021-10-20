package com.holland.gateway.controller;

import com.holland.common.utils.ValidateUtil;
import com.holland.common.utils.sqlHelper.PageHelper;
import com.holland.gateway.domain.Code;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.gateway.mapper.CodeTypeMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "码表模块")
@Controller
@RequestMapping("/code")
public class CodeController {

    @Resource
    private CodeMapper codeMapper;

    @Resource
    private CodeTypeMapper codeTypeMapper;

    @ApiOperation("获取某个类别代码下的所有条目")
    @GetMapping("/all")
    public ResponseEntity<?> all(String type) {
        ValidateUtil.notEmpty(type, "类别代码");
        return ResponseEntity.ok(codeMapper.all(type));
    }

    @ApiOperation("获取所有类别代码")
    @GetMapping("/type/list")
    public ResponseEntity<?> loginList(Integer page, Integer limit) {
        final PageHelper pageHelper = new PageHelper(page, limit);
        return ResponseEntity.ok(Map.of("data", codeTypeMapper.list(pageHelper), "count", codeTypeMapper.count()));
    }

    @ApiOperation("在某个类别代码下新增条目")
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Code code) {
        ValidateUtil.notEmpty(code.getType(), "类别代码");
        ValidateUtil.notEmpty(code.getCode(), "值");
        ValidateUtil.notEmpty(code.getName(), "名称");
        ValidateUtil.maxLength(code.getName(), 256, "名称");
        final int row = codeMapper.insert(code);
        return ResponseEntity.ok(row);
    }
}
