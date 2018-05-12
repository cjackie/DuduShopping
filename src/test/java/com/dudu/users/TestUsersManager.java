package com.dudu.users;

import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chaojiewang on 5/10/18.
 */
public class TestUsersManager {
    UsersManager manager;

    @Before
    public void setup() {
        try {
            String conf = System.getenv("DB_CONF");
            if (conf == null)
                conf = "./conf/db.conf";

            try (InputStream in = new FileInputStream(conf)) {
                Properties properties = new Properties();
                properties.load(in);

                DBManager.init(properties);
                manager = new UsersManager(DBManager.getManager().getDataSource("DuduShopping"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createUser() throws Exception {
        Assume.assumeTrue(manager != null);
        User user = manager.createUser("jack", "test123", UsersManager.USER_ROLE_CUSTOMER, UsersManager.SCOPE_CUSTOMER, "");

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }

    @Test
    public void login() throws Exception {
        Assume.assumeTrue(manager != null);
        User user = manager.login("jack", "test123", UsersManager.USER_ROLE_CUSTOMER);

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }


    @Test
    public void getUser() throws Exception {
        Assume.assumeTrue(manager != null);
        User user = manager.getUser("jack", "test123", UsersManager.USER_ROLE_CUSTOMER);

        System.out.println("UserId: " + user.getUserId());
        System.out.println("Login: " + user.getLogin());
    }
}
