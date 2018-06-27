package com.dudu.cache;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

public class FifoCache<T> implements Cache<T> {
    private ConcurrentHashMap<String, T> mapping;
    private Deque<String> fifo;
    private int maxSize;

    public FifoCache(int maxSize) {
        this.maxSize = maxSize;
        this.mapping = new ConcurrentHashMap<>();
        this.fifo = new ArrayDeque<>(maxSize);
    }

    @Override
    public T get(String key) {
        return mapping.get(key);
    }

    @Override
    public void cache(String key, T object) {
        if (mapping.get(key) == null) {
            if (fifo.size() == maxSize) {
                String evicted = fifo.removeFirst();
                mapping.remove(evicted);
            }

            fifo.addLast(key);
            mapping.put(key, object);
        }
    }
}
