package com.holland.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.holland.common.entity.json_rpc2.RPC;
import com.holland.common.entity.json_rpc2.Request;
import com.holland.common.utils.State;
import com.holland.gateway.common.RequestUtil;
import io.swagger.annotations.Api;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "JSON-RPC2")
@RestController
@RequestMapping("json-rpc2")
public class JsonRpc2Controller {

    private final Logger logger = LoggerFactory.getLogger(JsonRpc2Controller.class);

    @Resource
    private WebClient.Builder webClient;

    @PostMapping("/send")
    Publisher<?> get(ServerHttpRequest req, @RequestBody String requestBody) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(RequestUtil.AUTH_KEY, RequestUtil.getToken(req));
        headers.put(RequestUtil.USER_KEY, RequestUtil.getCacheUserStr(req));

        if (requestBody != null) {
            if (JSON.isValidArray(requestBody)) {
                final JSONArray objects = JSON.parseArray(requestBody);
                if (objects.size() > 0) {
                    final JSONObject object = objects.getJSONObject(0);
                    if ("2.0".equals(object.getString("jsonrpc"))) {
                        final List<Request> requests = objects.toJavaList(Request.class);
                        return Flux.fromArray(requests.toArray())
                                .parallel()
                                .runOn(Schedulers.boundedElastic())
                                .flatMap(request -> getRpcMono((Request) request, headers));
                    }
                }
            }
            if (JSON.isValidObject(requestBody)) {
                final JSONObject object = JSON.parseObject(requestBody);
                if ("2.0".equals(object.getString("jsonrpc"))) {
                    final Request request = object.toJavaObject(Request.class);
                    return getRpcMono(request, headers)
                            .map(ResponseEntity::ok);
                }
            }
        }
        return Mono.defer(() -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    private Mono<RPC> getRpcMono(Request request, Map<String, String> headers) {
        return webClient
                .build()
                .get()
                .uri("http://" + request.method)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(String.class)
                .map(State::new)
                .onErrorResume(e -> Mono.just(new State<>(e)))
                .map(s -> {
                    if (s.ok()) {
                        return new RPC.Success<>(request.id, s.val);
                    } else {
                        return new RPC.Error(request.id, 1, s.e.getMessage());
                    }
                });
    }
}
