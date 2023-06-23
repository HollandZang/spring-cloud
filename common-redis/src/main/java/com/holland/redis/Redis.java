package com.holland.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Redis {

    protected final String host;
    protected final int port;
    protected final JedisPool jedisPool;

    public Redis(String host, int port) {
        this.host = host;
        this.port = port;
        this.jedisPool = new JedisPool(host, port);
    }

    public <T> T exec(int index, Function<Jedis, T> action) {
        final Jedis jedis = jedisPool.getResource();
        jedis.select(index);
        final T apply = action.apply(jedis);
        jedisPool.returnResource(jedis);
        return apply;
    }

    public Lock lock(Class<?> clazz, String... keys) {
        return lock(-1, clazz, keys);
    }

    public Lock lock(int seconds, Class<?> clazz, String... keys) {
        final List<String> list = new ArrayList<>();
        list.add(clazz.getSimpleName());
        Collections.addAll(list, keys);
        return lock(seconds, list.toArray(String[]::new));
    }

    public Lock lock(String... lockName) {
        return lock(0, lockName);
    }

    public Lock lock(int seconds, String... lockName) {
        if (lockName.length == 0)
            throw new RuntimeException("lockName is empty!");

        final String key = Arrays.stream(lockName)
                .reduce((s, s2) -> s + Lock.DEFAULT_LOCK_SPLIT + s2)
                .orElse("null")
                + ".lock";
        if (seconds <= 0) seconds = Lock.DEFAULT_LOCK_SECONDS;
        final Jedis jedis = jedisPool.getResource();
        jedis.select(15);
        if (jedis.get(key) == null) {
            Long setnx = jedis.setnx(key, "1");
            if (setnx > 0) {
                jedis.expire(key, seconds);
                jedis.close();
                return new Lock(key, seconds, this, true);
            } else {
                jedis.close();
                return new Lock(key, seconds, this, false);
            }
        } else {
            jedisPool.returnResource(jedis);
            return new Lock(key, seconds, this, false);
        }
    }
}
