package com.holland.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
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

    public Lock lock(String... lockName) {
        return lock(0, lockName);
    }

    public Lock lock(int seconds, String... lockName) {
        final String key = Arrays.stream(lockName)
                .reduce((s, s2) -> s + Lock.DEFAULT_LOCK_SPLIT + s2)
                .orElse("null")
                + ".lock";
        if (seconds <= 0) seconds = Lock.DEFAULT_LOCK_SECONDS;
        final Jedis jedis = jedisPool.getResource();
        jedis.select(15);
        if (jedis.get(key) == null) {
            jedis.setex(key, seconds, "1");
            jedisPool.returnResource(jedis);
            return new Lock(key, seconds, this, true);
        } else {
            jedisPool.returnResource(jedis);
            return new Lock(key, seconds, this, false);
        }
    }
}
