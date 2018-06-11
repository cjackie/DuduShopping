package com.dudu.shop;

import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
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
     * @param userId who will be charged for the order
     * @param requestId
     * @param offerId
     * @throws Exception
     */
    public ShoppingOrder createOrder(long userId, long requestId, long offerId) throws Exception {
        ShoppingOffer offer = offerManager.getShoppingOffer(offerId);
        double price = offer.getPrice();
        if (price <= 0)
            throw new IllegalArgumentException("invalid price of offer " + offerId);

        long orderId;
        // create an order
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingOrderCreate_Limbo");
            sp.addParameter("ShoppingRequestId", requestId);
            sp.addParameter("ShoppingOfferId", offerId);

            List<ZetaMap> zetaMaps = sp.execToZetaMaps();
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("Failed to create an order: " + requestId + ", " + offerId);

            ZetaMap zetaMap = zetaMaps.get(0);
            int error = zetaMap.getInt("Error");
            if (error != 0)
                throw new IllegalArgumentException("Failed to create an order, with error code: " + error);

            orderId = zetaMap.getLong("OrderId");
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
     * @param begin can be null
     * @param end can be null
     * @return
     */
    public List<ShoppingOrder> searchOrders(long userId, Date begin, Date end) {
        try (Connection conn = source.getConnection()) {
            StoredProcedure sp = new StoredProcedure(conn, "sp_ShoppingOrderSearch");
            sp.addParameter("UserId", userId);
            sp.addParameter("Begin", begin);
            sp.addParameter("End", end);

            List<ZetaMap> zetaMaps = sp.execToZetaMaps();

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
                String warning = "Failed to update ShoppingOrders OrderId=" + orderId + " to shipped";
                logger.warn(warning);
                throw new IllegalArgumentException(warning);
            }

            return getOrder(orderId);
        }
    }

    /**
     *
     * @param orderId
     * @param tracking
     * @return
     * @throws Exception
     */
    public ShoppingOrder updateTrackingNumber(long orderId, String tracking) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "UPDATE ShoppingOrders SET ShipmentTrackingNumber = ? WHERE OrderId = ? AND OrderState = ?";
            int c = DBHelper.getHelper().execUpdate(conn, sql, tracking, orderId, ORDER_STATE_SHIPPED);
            if (c != 1) {
                String warning = "Failed to update tracking number";
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
