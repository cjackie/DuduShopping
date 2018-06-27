package com.dudu.cache;

import org.junit.Assert;
import org.junit.Test;

public class FifoCacheTest {

    @Test
    public void get() throws Exception {
        FifoCache<String> cache = new FifoCache<>(2);
        cache.cache("1", "hi1");
        cache.cache("2", "hi2");
        cache.cache("3", "hi3");

        Assert.assertEquals("hi2", cache.get("2"));
        Assert.assertEquals(null, cache.get("1"));
    }

}
