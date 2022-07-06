package com.holland.redis;

public class Lock implements AutoCloseable {
    private final String key;
    private final int seconds;
    private final Redis redis;
    private final boolean locked;

    public static int DEFAULT_LOCK_SECONDS = 60;
    public static char DEFAULT_LOCK_SPLIT = ':';

    public Lock(String key, int seconds, Redis redis, boolean locked) {
        this.key = key;
        this.seconds = seconds;
        this.redis = redis;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public void close() {
        if (locked) redis.exec(15, jedis -> jedis.del(key));
    }
}
