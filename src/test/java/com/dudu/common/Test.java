package com.dudu.common;

import com.dudu.database.DBManager;
import com.dudu.users.User;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Test extends TestBase {

    @org.junit.Test
    public void uriInfo() throws Exception {
        URI uri = new URI("http://www.google.com/foo/bar?ddd=3");
        println(uri.getPath());
    }

    @org.junit.Test
    public void split() throws Exception {
        String s = "/dudu_shopping/rest/auth/refreshToken";
        for (String t : s.split("/"))
            println(t);
    }

    @org.junit.Test
    public void sizeOfUser() throws Exception {
        List<User> users = new ArrayList<>();
        int size = 100000;
        long freeMemory = Runtime.getRuntime().freeMemory();
        for (int i = 0; i < size; i++) {
            User user = new User();
            user.setRawScopes("customer,sale" + i);
            users.add(user);
        }
        long freeMemory2 = Runtime.getRuntime().freeMemory();

        println("Each user is size of " + ((freeMemory - freeMemory2)/size) + " bytes");
    }

    @org.junit.Test
    public void redisDb() throws Exception {
        JedisPool pool = DBManager.getManager().getChatRoomRedisPool();
        println(pool.getResource());
        println(pool.getResource().ping());
        pool.getResource().hset("jacktest", "t", "3");

        println(pool.getResource().hget("unknown1", "t") == null);
    }

    @org.junit.Test
    public void className() {
        println(Test.class.getName());
    }
}
