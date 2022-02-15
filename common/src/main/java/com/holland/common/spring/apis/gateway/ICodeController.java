package com.holland.common.spring.apis.gateway;

import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Api(tags = "码表模块")
@RequestMapping("/code")
public interface ICodeController {

    @ApiOperation("获取某个类别代码下的所有条目")
    @GetMapping("/all")
    Mono<Response<List<Map<String, String>>>> all(String type);

    @ApiOperation("获取所有类别代码")
    @GetMapping("/type/list")
    Mono<Response<List<CodeType>>> loginList(Integer page, Integer limit);

    @ApiOperation("在某个类别代码下新增条目")
    @PostMapping
    Mono<Response<Integer>> add(@RequestBody Code code);
}
