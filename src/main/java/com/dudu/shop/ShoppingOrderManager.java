package com.dudu.shop;

import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import com.dudu.payment.StripeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ShoppingOrderManager {
    public static final String ORDER_STATE_LIMBO = "OS0";
    public static final String ORDER_STATE_PAID = "OS5";
    public static final String ORDER_STATE_SHIPPED = "OS10";
    public static final String ORDER_STATE_DONE = "OS20";

    private static final Logger logger = LogManager.getLogger(ShoppingOfferManager.class);
    private DataSource source;
    private ShoppingOfferManager offerManager;
    private StripeManager stripeManager;


    public ShoppingOrderManager(DataSource source) {
        this.source = source;
        this.offerManager = new ShoppingOfferManager(source);
        this.stripeManager = new StripeManager(source);
    }

    /**
     *
     * @param userId
     * @param requestId
     * @param offerId
     * @throws Exception
     */
    public ShoppingOrder createOrder(long userId, long requestId, long offerId) throws Exception {
        ShoppingOffer offer = offerManager.getShoppingOffer(offerId);
        double price = offer.getPrice();
        if (price <= 0)
            throw new IllegalArgumentException("invalid price of offer " + offerId);

        // create an order
        long orderId;
        try (Connection conn = source.getConnection()) {
            String insert = "INSERT INTO ShoppingOrders(ShoppingRequestId, ShoppingOfferId, OrderState) VALUES (?,?,?)";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execUpdateToZetaMaps(conn, insert, new String[] {"OrderId"}, requestId, offerId, ORDER_STATE_LIMBO);

            orderId = zetaMaps.get(0).getLong("OrderId");
        }

        // pay it
        stripeManager.charge(orderId, userId, Math.round(price*100));

        // update the order to paid state
        try (Connection conn = source.getConnection()) {
            String update = "UPDATE ShoppingOrders SET OrderState = ? WHERE OrderId = ?";
            int count = DBHelper.getHelper().execUpdate(conn, update, ORDER_STATE_PAID, orderId);

            if (count == 0) {
                logger.error("Failed to update the order to PAID state");
                throw new IllegalStateException("Failed to update the order to PAID state");
            }
        }

        // getting the order object
        return getOrder(orderId);
    }

    ShoppingOrder getOrder(long orderId) throws SQLException {
        // getting the order object
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM ShoppingOrders WHERE OrderId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, orderId);

            ShoppingOrder order = ShoppingOrder.from(zetaMaps.get(0));
            return order;
        }
    }

    /**
     *
     * @param userId
     * @param start can be null
     * @param end can be null
     * @return
     */
    public List<ShoppingOrder> searchOrders(long userId, Date start, Date end) {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM ShoppingOffers WHERE UserId = ? ";

            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (start != null) {
                sql += " AND CreatedAt >= ? ";
                params.add(start);
            }

            if (end != null) {
                sql += " AND CreatedAt <= ? ";
                params.add(start);
            }

            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, params.toArray());

            List<ShoppingOrder> orders = new ArrayList<>();
            for (ZetaMap zetaMap : zetaMaps)
                orders.add(ShoppingOrder.from(zetaMap));

            return orders;
        } catch (Exception e) {
            logger.warn("Failed to searchOrders", e);
            return Collections.emptyList();
        }
    }

    public ShoppingOrder orderShipped(long orderId) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "UPDATE ShoppingOrders SET OrderState = ? WHERE OrderId = ? AND OrderState = ?";

            int count = DBHelper.getHelper().execUpdate(conn, sql, ORDER_STATE_SHIPPED, orderId, ORDER_STATE_PAID);
            if (count != 1) {
                String warning = "Failed to update ShoppingOrder OrderId=" + orderId + " to shipped";
                logger.warn(warning);
                throw new IllegalArgumentException(warning);
            }

            return getOrder(orderId);
        }
    }

    public ShoppingOrder orderDone(long orderId) throws Exception {
        try (Connection conn = source.getConnection()) {
            String update = "UPDATE ShoppingOrders SET OrderState = ? WHERE OrderId = ? AND OrderState = ?";

            int count = DBHelper.getHelper().execUpdate(conn, update, ORDER_STATE_DONE, orderId, ORDER_STATE_SHIPPED);
            if (count != 1) {
                String warning = "Failed to update ShoppingOrder OrderId=" + orderId + " to done";
                logger.warn(warning);
                throw new IllegalArgumentException(warning);
            }

            return getOrder(orderId);
        }
    }


}
