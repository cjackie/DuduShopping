package com.dudu.shop;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import com.dudu.payment.StripeProxy;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;

public class ShoppingOrderManagerTest extends TestBase {
    ShoppingOrderManager manager;

    @Before
    public void setup() {
        super.setup();

        if (dbReady) {

            DataSource source = DBManager.getManager().getDataSource("DuduShopping");
            String apiKey = System.getenv("STRIPE_API_KEY");
            if (source == null || apiKey == null) {
                ready = false;
                return;
            }

            StripeProxy.configure(apiKey);
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

    @Test
    public void searchOrders() throws Exception {
        Assume.assumeTrue(ready);

        long userId = 1;
        List<ShoppingOrder> orders = manager.searchOrders(userId, null, null);
        println(orders.size());
    }

    @Test
    public void orderShipped() throws Exception {
        Assume.assumeTrue(ready);

        long orderId = 1;
        ShoppingOrder order = manager.orderShipped(orderId);
        println(order.getOrderState());
    }

    @Test
    public void updateTrackingNumber() throws Exception {
        Assume.assumeTrue(ready);

        long orderId = 1;
        String trackingNumber = "UPS123456789";

        ShoppingOrder order = manager.updateTrackingNumber(orderId, trackingNumber);
        println(order.getShipmentTrackingNumber());
    }

    @Test
    public void orderDone() throws Exception {
        Assume.assumeTrue(ready);

        long orderId = 1;
        ShoppingOrder order = manager.orderDone(orderId);
        println(order.getOrderState());
    }

}
