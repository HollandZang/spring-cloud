package com.holland.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lock implements AutoCloseable {
    private final String key;
    private final int seconds;
    private final Redis redis;
    private final boolean locked;
    private final long cTime = System.currentTimeMillis();

    public static int DEFAULT_LOCK_SECONDS = 60;
    public static char DEFAULT_LOCK_SPLIT = ':';
    private static final Logger logger = LoggerFactory.getLogger(Lock.class);

    public Lock(String key, int seconds, Redis redis, boolean locked) {
        this.key = key;
        this.seconds = seconds;
        this.redis = redis;
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean tryClose() {
        if (locked) {
            final long useSeconds = (System.currentTimeMillis() - cTime) / 1000;
            if (useSeconds > seconds) {
                logger.error("Hold lock timeout, key={}, time={}s, holdSeconds={}", key, seconds, useSeconds);
                return false;
            }

            final long l = redis.exec(15, jedis -> jedis.del(key));
            if (l == 0) {
                logger.error("Release lock failed, key={}", key);
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() {
        if (locked) {
            final long useSeconds = (System.currentTimeMillis() - cTime) / 1000;
            if (useSeconds > seconds) {
                logger.error("Hold lock timeout, key={}, time={}s, holdSeconds={}", key, seconds, useSeconds);
                return;
            }

            final long l = redis.exec(15, jedis -> jedis.del(key));
            if (l == 0) {
                logger.error("Release lock failed, key={}", key);
            }
        }
    }
}
