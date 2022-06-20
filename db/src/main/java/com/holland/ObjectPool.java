package com.holland;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class ObjectPool<T> {
    private final long expirationTime;
    private final TimeUnit timeUnit;

    private final ConcurrentHashMap<T, Long> locked, unlocked;

    public ObjectPool() {
        this.expirationTime = 30L;
        this.timeUnit = TimeUnit.SECONDS;

        this.locked = new ConcurrentHashMap<>();
        this.unlocked = new ConcurrentHashMap<>();
    }

    protected abstract T create();

    protected abstract boolean validate(T o);

    protected abstract void expire(T o);

    public synchronized T checkOut() {
        final long now = System.currentTimeMillis();
        T t;
        if (unlocked.size() > 0) {
            for (Map.Entry<T, Long> entry : unlocked.entrySet()) {
                t = entry.getKey();
                final Long createTime = entry.getValue();
                if (now - createTime > timeUnit.toMillis(expirationTime)) {
                    unlocked.remove(t);
                    expire(t);
                    t = null;
                } else {
                    if (validate(t)) {
                        unlocked.remove(t);
                        locked.put(t, now);
                        return t;
                    } else {
                        unlocked.remove(t);
                        expire(t);
                        t = null;
                    }
                }
            }
        }
        t = create();
        locked.put(t, now);
        return t;
    }

    public synchronized void checkIn(T t) {
        locked.remove(t);
        unlocked.put(t, System.currentTimeMillis());
    }

    public synchronized void close() {
        while (locked.size() > 0) {
            System.out.println("waiting for running object");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final Enumeration<T> keys = unlocked.keys();
        while (keys.hasMoreElements()) {
            expire(keys.nextElement());
        }
    }
}
