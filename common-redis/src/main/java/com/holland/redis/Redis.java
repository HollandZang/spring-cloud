package com.holland.redis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

    protected <T> T exec(int index, Function<Jedis, T> action) {
        final Jedis jedis = jedisPool.getResource();
        jedis.select(index);
        final T apply = action.apply(jedis);
        jedisPool.returnResource(jedis);
        return apply;
    }
}
