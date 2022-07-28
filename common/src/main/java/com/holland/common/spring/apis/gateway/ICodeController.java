package com.holland.common.spring.apis.gateway;

import com.holland.common.entity.gateway.Code;
import com.holland.common.entity.gateway.CodeType;
import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Api(tags = "码表模块")
@RequestMapping("/code")
public interface ICodeController {

    @ApiOperation("获取所有类别代码")
    @GetMapping("/type")
    Mono<Response<List<CodeType>>> typeAll();

    @ApiOperation("新增类别")
    @PostMapping("/type")
    Mono<Response<Integer>> typeAdd(ServerHttpRequest request, @RequestBody CodeType codeType);

    @ApiOperation("获取某个类别代码下的所有条目")
    @GetMapping("/{type}")
    Mono<Response<List<Code>>> all(@PathVariable String type);

    @ApiOperation("在某个类别代码下新增条目")
    @PostMapping
    Mono<Response<Integer>> add(ServerHttpRequest request, @RequestBody Code code);
}
