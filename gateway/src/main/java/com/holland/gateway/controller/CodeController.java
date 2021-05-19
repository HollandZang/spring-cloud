package com.holland.gateway.controller;

import com.holland.gateway.common.ValidateUtil;
import com.holland.gateway.domain.Code;
import com.holland.gateway.mapper.CodeMapper;
import com.holland.gateway.mapper.CodeTypeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/code")
public class CodeController {

    @Resource
    private CodeMapper codeMapper;

    @Resource
    private CodeTypeMapper codeTypeMapper;

    @GetMapping("/all")
    public ResponseEntity<?> all(String type) {
        ValidateUtil.notEmpty(type, "类别代码");
        return ResponseEntity.ok(codeMapper.all(type));
    }

    @GetMapping("/type/list")
    public ResponseEntity<?> loginList(Integer page, Integer limit) {
        final int offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * limit;
        limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        return ResponseEntity.ok(Map.of("data", codeTypeMapper.list(offset, limit), "count", codeTypeMapper.count()));
    }

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
