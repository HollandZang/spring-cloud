package com.holland.gateway.route_white_list;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping("/config")
public class RouteWhitelistController {

    @Resource
    private RouteWhitelistMapper routeWhitelistMapper;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody RouteWhitelist routeWhitelist) {
        if (routeWhitelist.getUrl() == null || routeWhitelist.getUrl().isEmpty()) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("url不能为空");
        }
        final Optional<RouteWhitelist> optional = routeWhitelistMapper.getByUrl(routeWhitelist.getUrl());
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("url已存在");
        }
        final int row = routeWhitelistMapper.insert(routeWhitelist);
        return ResponseEntity.ok(row);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody RouteWhitelist routeWhitelist) {
        if ((routeWhitelist.getUrl() == null || routeWhitelist.getUrl().isEmpty())
                && (routeWhitelist.getEnabled() == null)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("接受参数为空");
        }

        final int row = routeWhitelistMapper.update(routeWhitelist);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        return ResponseEntity.ok(row);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> del(@PathVariable("id") Integer id) {
        final int row = routeWhitelistMapper.del(id);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        return ResponseEntity.ok(row);
    }
}
