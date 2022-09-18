package com.holland.gateway.common;

import com.alibaba.fastjson.JSON;
import com.holland.common.utils.Comm;
import com.holland.redis.Lock;
import com.holland.redis.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CommCache {
    private final Logger logger = LoggerFactory.getLogger(Redis.class);

    protected final Redis redis;

    public CommCache(Redis redis) {
        this.redis = redis;
    }

    public Lock r_lock(ServerHttpRequest request, String... keys) {
        final List<String> list = new ArrayList<>();
        list.add(RequestUtil.getReqLine(request));
        Collections.addAll(list, keys);
        return redis.lock(list.toArray(new String[0]));
    }

    public <T> T r_getOrReassign(Supplier<T> supplier, String... key) {
        return r_getOrReassign(supplier, -1, key);
    }

    public <T> T r_getOrReassign(Supplier<T> supplier, long seconds, String... key) {
        T o = r_get(key);
        if (o != null) return o;

        o = supplier.get();
        r_set(o, seconds, key);
        return o;
    }

    public <T> void r_set(T value, String... key) {
        r_set(value, -1, key);
    }

    public <T> void r_set(T value, long seconds, String... key) {
        final String k = Comm.concatStr(key);
        String val = value == null ? null : JSON.toJSONString(value);
        redis.exec(0, jedis -> seconds == -1
                ? jedis.set(k, val)
                : jedis.setex(k, seconds, val));
        logger.info("r_set {}={}", k, val);
    }

    public <T> T r_get(String... key) {
        final String k = Comm.concatStr(key);
        final String exec = redis.exec(0, jedis -> jedis.get(k));
        final Object parse = JSON.parse(exec);
        logger.info("r_get {}={}", k, exec);
        return (T) parse;
    }

    public void r_remove(String... key) {
        final String k = Comm.concatStr(key);
        redis.exec(0, jedis -> jedis.del(k));
        logger.info("r_remove {}", k);
    }
}
