package com.holland.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class Ehcache {
    private CacheManager cacheManager;
    private Cache<Integer, Integer> squareNumberCache;

    public Ehcache() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().build();
        cacheManager.init();

        squareNumberCache = cacheManager
                .createCache("squaredNumber", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                Integer.class, Integer.class,
                                ResourcePoolsBuilder.heap(10)));
    }

    public Cache<Integer, Integer> getSquareNumberCache() {
        return cacheManager.getCache("squaredNumber", Integer.class, Integer.class);
    }
}
