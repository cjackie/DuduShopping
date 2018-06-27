package com.dudu.cache;

public interface Cache<T> {
    T get(String key);
    void cache(String key, T object);
}
