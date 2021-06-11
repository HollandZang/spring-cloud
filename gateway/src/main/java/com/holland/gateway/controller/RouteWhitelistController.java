package com.holland.gateway.controller;

import com.holland.gateway.common.CustomCache;
import com.holland.gateway.common.ValidateUtil;
import com.holland.gateway.domain.RouteWhitelist;
import com.holland.gateway.mapper.RouteWhitelistMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("/list")
    public ResponseEntity<?> list(Integer page, Integer limit) {
        final int offset = page == null ? 0 : page <= 0 ? 0 : (page - 1) * limit;
        limit = limit == null ? 10 : limit <= 0 ? 10 : limit;
        final List<RouteWhitelist> collect = customCache.routeWhitelist.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("data", collect, "count", customCache.routeWhitelist.size()));
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody RouteWhitelist routeWhitelist) {
        ValidateUtil.notEmpty(routeWhitelist.getUrl(), "url");
        ValidateUtil.maxLength(routeWhitelist.getUrl(), 256, "url");

        final Optional<RouteWhitelist> optional = routeWhitelistMapper.selectByUrl(routeWhitelist.getUrl());
        if (optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("url已存在");
        }
        final int row = routeWhitelistMapper.insertSelective(routeWhitelist);
        customCache.init();
        return ResponseEntity.ok(row);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody RouteWhitelist routeWhitelist) {
        ValidateUtil.maxLength(routeWhitelist.getUrl(), 256, "url");

        final int row = routeWhitelistMapper.updateByPrimaryKeySelective(routeWhitelist);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        customCache.init();
        return ResponseEntity.ok(row);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> del(@PathVariable("id") Long id) {
        final int row = routeWhitelistMapper.deleteByPrimaryKey(id);
        if (row == 0) {
            return ResponseEntity.status(HttpStatus.GONE).body("资源不存在");
        }
        customCache.init();
        return ResponseEntity.ok(row);
    }
}
