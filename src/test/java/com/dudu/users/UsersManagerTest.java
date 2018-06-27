package com.dudu.users;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

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
            if (!dbReady || source == null)
                return;

            manager = new UsersManager(source);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createUser() throws Exception {
        Assume.assumeTrue(manager != null);
//        User user = manager.createUser("jack", "test123", UsersManager.USER_ROLE_CUSTOMER, UsersManager.SCOPE_CUSTOMER, "");
        User user = manager.createUser("saleAgent4", "test123", UsersManager.USER_ROLE_SALE_AGENT, UsersManager.SCOPE_CUSTOMER, "");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }

    @Test
    public void login() throws Exception {
        Assume.assumeTrue(manager != null);
        User user = manager.login("jack", "test123");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }


    @Test
    public void getUser() throws Exception {
        Assume.assumeTrue(manager != null);
        User user = manager.getUser("jack", "test123");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());

        long userId = 1;
        user = manager.getUser(userId);
        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());

        user = manager.getUser(userId);
        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }
}
