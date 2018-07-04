package com.dudu.users;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

/**
 * Created by chaojiewang on 5/10/18.
 */
public class UsersManagerTest extends TestBase {
    UsersManager manager;

    @Before
    public void setup() {
        super.setup();
        try {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            JedisPool cache = DBManager.getManager().getCacheRedisPool();
            if (!dbReady || source == null || cache == null)
                return;

            manager = new UsersManager(source, cache);
        } catch (Exception e) {
            System.out.println(e);
        }

        Assume.assumeTrue(ready);
    }

    @Test
    public void createUser() throws Exception {
//        User user = manager.createUser("jack", "test123", UsersManager.USER_ROLE_CUSTOMER, UsersManager.SCOPE_CUSTOMER, "");
        User user = manager.createUser("saleAgent4", "test123", UsersManager.USER_ROLE_SALE_AGENT, UsersManager.SCOPE_CUSTOMER, "");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }

    @Test
    public void login() throws Exception {
        User user = manager.login("jack", "test123");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }


    @Test
    public void getUser() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i ++) {
            long userId = 1;
            User user = manager.getUser(userId);
            System.out.println("UserId: " + user.getUserId());
            System.out.println("Login: " + user.getLogin());

            user = manager.getUser(userId);
            System.out.println("UserId: " + user.getUserId());
            System.out.println("Login: " + user.getLogin());
        }
        println("time: " + (System.currentTimeMillis() - start));
    }
}
