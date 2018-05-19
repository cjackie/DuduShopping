package com.dudu.shop;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import com.dudu.users.User;
import com.dudu.users.UsersManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * Created by chaojiewang on 5/15/18.
 */
public class ShoppingRequestManagerTest extends TestBase {
    ShoppingRequestManager shoppingRequestManager;
    UsersManager usersManager;

    @Before
    public void setup() {
        super.setup();

        try {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            if (!dbReady || source == null)
                return;

            shoppingRequestManager = new ShoppingRequestManager(source);
            usersManager = new UsersManager(source);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void createRequest() throws Exception {
        Assume.assumeTrue(shoppingRequestManager != null);

        User jack = usersManager.getUser(1);
        ShoppingRequest request = shoppingRequestManager.createRequest(jack, "Want a toy 2");
        println(request);
    }

    @Test
    public void cancelRequest() throws Exception {
        Assume.assumeTrue(shoppingRequestManager != null);

        User jack = usersManager.getUser(1);
        long requestId = 10;
        int error = shoppingRequestManager.cancelRequest(jack, requestId);
        println(error);
    }

    @Test
    public void acceptRequest() throws Exception {
        User jack = usersManager.getUser(1);
        long requestId = 5;
        long offerId = 1;
        int error = shoppingRequestManager.acceptRequest(jack, requestId, offerId);
        println(error);
    }
}
