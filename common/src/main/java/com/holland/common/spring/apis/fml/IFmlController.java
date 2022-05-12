package com.holland.common.spring.apis.fml;

import com.holland.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Api(tags = "Fml模块")
@RequestMapping("/fml")
public interface IFmlController {

    @ApiOperation("获取Fml")
    @GetMapping("/{id}")
    Mono<Response<List<Map<String, String>>>> findById(@PathVariable Integer id);
}
