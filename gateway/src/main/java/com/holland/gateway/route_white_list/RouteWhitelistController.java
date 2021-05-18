package com.holland.gateway.route_white_list;

import com.holland.gateway.common.CustomCache;
import com.holland.gateway.common.ValidateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/route/whitelist")
public class RouteWhitelistController {

    @Resource
    private RouteWhitelistMapper routeWhitelistMapper;

    @Resource
    private CustomCache customCache;

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh() {
        customCache.init();
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody RouteWhitelist routeWhitelist) {
        ValidateUtil.validateNotEmpty(routeWhitelist.getUrl(), "url");
        ValidateUtil.validateLength(routeWhitelist.getUrl(), 256, "url");

        final Optional<RouteWhitelist> optional = routeWhitelistMapper.getByUrl(routeWhitelist.getUrl());
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("url已存在");
        }
        final int row = routeWhitelistMapper.insert(routeWhitelist);
        customCache.init();
        return ResponseEntity.ok(row);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody RouteWhitelist routeWhitelist) {
        ValidateUtil.validateLength(routeWhitelist.getUrl(), 256, "url");

        final int row = routeWhitelistMapper.update(routeWhitelist);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        customCache.init();
        return ResponseEntity.ok(row);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> del(@PathVariable("id") Integer id) {
        final int row = routeWhitelistMapper.del(id);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        customCache.init();
        return ResponseEntity.ok(row);
    }
}
