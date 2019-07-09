package com.play.english.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author chaiqx on 2019/6/11
 */
public class MemCache<K, V> {

    private Cache<K, V> underlying;

    public MemCache(int cacheSize) {
        this(cacheSize, 16);
    }

    public MemCache(int cacheSize, int concurrencyLevel) {
        this(cacheSize, concurrencyLevel, 0L);
    }

    public MemCache(int cacheSize, int concurrencyLevel, long ttl) {
        if (concurrencyLevel <= 0) {
            concurrencyLevel = 1;
        }
        if (ttl <= 0) {
            underlying = CacheBuilder.newBuilder().maximumSize(cacheSize)
                    .concurrencyLevel(concurrencyLevel).build();
        } else {
            underlying = CacheBuilder.newBuilder().maximumSize(cacheSize)
                    .concurrencyLevel(concurrencyLevel).expireAfterWrite(ttl, TimeUnit.MILLISECONDS).build();
        }
    }

    public V get(final K key) {
        return underlying.getIfPresent(key);
    }

    /**
     * get一个key的value，假如不存在，则put一下返回null，否则返回oldValue
     *
     * @param key
     * @param value
     * @return
     */
    public V getOrPutIfAbsent(final K key, final V value) {
        final AtomicBoolean exist = new AtomicBoolean(true);
        V tmpValue = null;
        try {
            tmpValue = underlying.get(key, () -> {
                exist.set(false);
                return value;
            });
        } catch (Exception e) {
            underlying.put(key, value);
            return null;
        }
        if (exist.get()) {
            return tmpValue;
        }
        return null;
    }

    public void put(final K key, final V value) {
        underlying.put(key, value);
    }

    /**
     * 从cache里删除
     *
     * @param key
     */
    public void remove(final K key) {
        underlying.invalidate(key);
    }

    /**
     * 返回cache当前的大小
     *
     * @return
     */
    public long size() {
        return underlying.size();
    }
}
