package com.holland.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.holland.common.entity.json_rpc2.Request;
import com.holland.common.entity.json_rpc2.Response;
import com.holland.gateway.common.RequestUtil;
import com.holland.net.Net;
import com.holland.net.common.PairBuilder;
import com.holland.net.conf.DefaultHttpConf;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Api(tags = "JSON-RPC2")
@RestController
@RequestMapping("json-rpc2")
public class JsonRpc2Controller {
    @Value("${server.port}")
    private String port;

    private final String url = "http://localhost:" + port + "/";

    private final Logger logger = LoggerFactory.getLogger(JsonRpc2Controller.class);

    final Net net = new Net(new DefaultHttpConf() {
        @Override
        public void printError(String s, Object... args) {
            logger.error(s, args);
        }
    });

    @GetMapping("/get")
    Mono<ResponseEntity<Response<?>>> get(ServerHttpRequest req, String requestBody) {
        final String token = RequestUtil.getToken(req);
        final PairBuilder headers = new PairBuilder().add("HAuth", token);
        if (requestBody != null) {
            if (JSON.isValidArray(requestBody)) {
                final JSONArray objects = JSON.parseArray(requestBody);
                if (objects.size() > 0) {
                    final JSONObject object = objects.getJSONObject(0);
                    if ("2.0".equals(object.getString("jsonrpc"))) {
                        final List<Request> requests = objects.toJavaList(Request.class);
                        for (final Request request : requests) {
//                            net.async.postJson(url + request.method, headers, request.params
//                                    , response -> {
//                                    });
                        }
                    }
                }
            }
            if (JSON.isValidObject(requestBody)) {
                final JSONObject object = JSON.parseObject(requestBody);
                if ("2.0".equals(object.getString("jsonrpc"))) {
                    final Request request = object.toJavaObject(Request.class);
                    final Optional<String> s = net.sync.postJson(url + request.method, headers, request.params);
                    return Mono.defer(() -> Mono.just(ResponseEntity.ok(
                            new Response<>("2.0", request.id, s.orElse(null), null)
                    )));
                }
            }
        }
        return Mono.defer(() -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
}
