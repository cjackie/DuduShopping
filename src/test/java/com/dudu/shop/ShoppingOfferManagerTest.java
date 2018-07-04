package com.dudu.shop;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import com.dudu.users.User;
import com.dudu.users.UsersManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

/**
 * Created by chaojiewang on 5/19/18.
 */
public class ShoppingOfferManagerTest extends TestBase {
    ShoppingOfferManager shoppingOfferManager;
    User saleAgent;
    UsersManager usersManager;

    @Before
    public void setup() {
        super.setup();

        try {
            if (dbReady) {
                DataSource source = DBManager.getManager().getDataSource("DuduShopping");
                JedisPool cache = DBManager.getManager().getCacheRedisPool();
                usersManager = new UsersManager(source, cache);
                saleAgent = usersManager.getUser(3);
                shoppingOfferManager = new ShoppingOfferManager(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pull() throws Exception {
    Assume.assumeTrue(shoppingOfferManager != null);
        int error = shoppingOfferManager.pull(saleAgent, 2);
        println(error);
    }

    @Test
    public void reject() throws Exception {
        User jack = usersManager.getUser(1);
        int error = shoppingOfferManager.reject(jack, 2);
        println(error);
    }

    @Test
    public void create() throws Exception {
        Assume.assumeTrue(shoppingOfferManager != null);
        ShoppingOffer offer = new ShoppingOffer();
        offer.setPrice(100);
        offer.setShoppingRequestId(10);
        offer.setText("I have an offer");
        ShoppingOffer offerMade = shoppingOfferManager.create(saleAgent, offer);

        println(offerMade.getShoppingOfferId());
    }
}
