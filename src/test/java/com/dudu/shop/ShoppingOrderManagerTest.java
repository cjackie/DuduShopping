package com.dudu.shop;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Test;

import javax.sql.DataSource;

public class ShoppingOrderManagerTest extends TestBase {
    // TODO
    ShoppingOrderManager manager;

    @Test
    public void setup() {
        super.setup();

        if (dbReady) {

            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            if (source == null) {
                ready = true;
                return;
            }

            manager = new ShoppingOrderManager(source);
        }
    }

    @Test
    public void createOrder() throws Exception {
        Assume.assumeTrue(ready);

        //long userId, long requestId, long offerId
        long userId = 1;
        long requestId = 1;
        long offerId = 1;

        ShoppingOrder order = manager.createOrder(userId, requestId, offerId);
        println(order);
    }

}
