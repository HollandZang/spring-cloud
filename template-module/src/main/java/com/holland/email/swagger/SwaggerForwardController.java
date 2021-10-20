package com.holland.email.swagger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger2.web.Swagger2ControllerWebFlux;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * swagger转发控制器
 * 为knife4j配置url前缀
 */
@Profile("!pro")
@Controller
public class SwaggerForwardController {

    @Resource
    private Swagger2ControllerWebFlux swagger2ControllerWebFlux;

    @GetMapping("/swagger/v2/api-docs")
    public ResponseEntity<?> forward(@RequestParam(value = "group", required = false) String swaggerGroup,
                                     ServerHttpRequest request) {
        final ResponseEntity<Json> documentation = swagger2ControllerWebFlux.getDocumentation(swaggerGroup, request);

        final Map<String, Object> map = JSON.parseObject(documentation.getBody().value(), Map.class);

        map.computeIfPresent("basePath", (k, pathsObj) -> "/filesystem");
        map.computeIfPresent("paths", (k, pathsObj) -> {
            final Map<String, JSONObject> paths = ((JSONObject) pathsObj).toJavaObject(Map.class);
            final Map<String, JSONObject> res = new HashMap<>(paths.size());
            paths.forEach((k1, v) -> res.put("/filesystem" + k1, v));
            return res;
        });

        return ResponseEntity.ok(map);
    }
}
