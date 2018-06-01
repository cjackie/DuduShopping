package com.dudu.payment;

import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaojiewang on 5/26/18.
 */
public class StripeManager {
    private static final int LOCK_ADD_SOURCE = 100;
    private static final int LOCK_CREATE_CUSTOMER = 200;
    private static final int LOCK_SET_PAYMENT_METHOD = 300;
    private static Logger logger = LogManager.getLogger(StripeManager.class);
    private DataSource source;

    public StripeManager(DataSource source) {
        this.source = source;
    }

    /**
     * condition: @sourceId needs to be added by <Link>addSource</Link>
     *
     * @param userId
     * @param sourceId
     * @throws Exception
     */
    public void setPaymentMethod(long userId, String sourceId) throws Exception {
        // get customer ID
        String customerId = getCustomerId(userId);

        if (customerId == null)
            throw new IllegalArgumentException("User " + userId + " has no source " + sourceId);

        if (isLocked(userId))
            throw new IllegalArgumentException("Payment of User " + userId + " is locked. It needs human help.");

        // make sure database has a record of sourceId
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM StripeSources WHERE UserId = ? AND SourceId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, userId, sourceId);
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("User " + userId + " with source " + sourceId + " is not found in database");
        }

        // update stripe, and then database
        StripeProxy.getProxy().setDefaultPaymentMethod(customerId, sourceId);
        try (Connection conn = source.getConnection()) {
            String sql = "UPDATE StripeSources SET SourceId = ? WHERE UserId = ?";
            int count = DBHelper.getHelper().execUpdate(conn, sql, sourceId, userId);
            if (count != 1) {
                lock(userId, LOCK_SET_PAYMENT_METHOD);
                logger.error("Failed to set default payment method: userId = " + userId + ", sourceId = " + sourceId);
                throw new IllegalStateException("Failed to update StripeSources");
            }
        }
    }

    /**
     *
     * @param userId
     * @param token
     * @param last4
     * @param expMonth
     * @param expYear
     * @param funding
     * @param brand
     * @throws Exception
     */
    public void addSource(long userId, String token, String last4, int expMonth, int expYear, String funding, String brand) throws Exception {
        // get customer ID
        String customerId = getCustomerId(userId);

        boolean newCustomer = customerId == null;
        if (customerId == null) {
            // need to create one
            customerId = createCustomer(userId);
        }

        // is it locked?
        if (isLocked(userId))
            throw new IllegalArgumentException("UserId " + userId + " is locked. It needs human help.");

        // create sourceId and save it
        String sourceId = StripeProxy.getProxy().addSource(customerId, token);
        try (Connection conn = source.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO StripeSource (UserId, SourceId, LastFour, ExpMonth, ExpYear, Funding, Branch, IsDefault) VALUES (?,?,?,?,?,?,?,?)")) {
                ps.setObject(1, userId);
                ps.setObject(2, sourceId);
                ps.setObject(3, last4);
                ps.setObject(4, expMonth != 0 ? expMonth : null);
                ps.setObject(5, expYear != 0 ? expYear : null);
                ps.setObject(6, funding);
                ps.setObject(7, brand);
                ps.setObject(8, newCustomer ? 1 : 0);
                int count = DBHelper.getHelper().execUpdate(ps);
                if (count != 1) {
                    lock(userId, LOCK_ADD_SOURCE);
                    logger.error("Failed to update StripeSources. UserId = " + userId + ", SourceId = " + sourceId);
                    throw new IllegalArgumentException("Failed to add source");
                }
            }
        }
    }

    /**
     *
     * @return could be null
     * @throws Exception
     */
    private String getCustomerId(long userId) throws Exception {
        // get customer ID
        String customerId = null;
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM StripeCustomers WHERE UserId = ? ";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, userId);
            if (zetaMaps.size() != 0) {
                customerId = zetaMaps.get(0).getString("CustomerId");
            }
        }

        return customerId;
    }

    /**
     *
     * @param userId
     * @return customer ID
     * @throws Exception
     */
    synchronized private String createCustomer(long userId) throws Exception {
        // need to create one
        String customerId = StripeProxy.getProxy().createCustomer("UserId = " + userId);
        try (Connection conn = source.getConnection()) {
            String sql = "INSERT INTO StripeCustomers (UserId, CustomerId) VALUES (?,?) ";

            int count = DBHelper.getHelper().execUpdate(conn, sql, userId, customerId);
            if (count != 1) {
                lock(userId, LOCK_CREATE_CUSTOMER);
                logger.error("Failed to update StripeCustomers: UserId=" + userId + ", CustomerId" + customerId);
                throw new IllegalArgumentException("Failed to add source");
            }
        }

        return customerId;
    }

    /**
     * condition: userId exists in StripeCustomers table.
     *
     * @param userId
     * @return
     * @throws Exception
     */
    private boolean isLocked(long userId) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT LockedReasonCode FROM StripeCustomers WHERE UserId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, userId);
            return zetaMaps.get(0).getInt("LockedReasonCode") != 0;
        }
    }

    /**
     * condition: userId exists in StripeCustomers table.
     *
     * @param userId
     * @return
     * @throws Exception
     */
    private void lock(long userId, int reasonCode) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "UPDATE StripeCustomers SET LockedReasonCode = ? WHERE UserId = ?";
            int count = DBHelper.getHelper().execUpdate(conn, sql, reasonCode, userId);
            if (count != 1)
                throw new IllegalArgumentException("Failed to lock User " + userId + " with reason code " + reasonCode);
        }
    }

    /**
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public StripeCustomer getCustomer(long userId) throws Exception {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM StripeCustomers WHERE UserId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql , userId);
            if (zetaMaps.size() != 1)
                throw new IllegalArgumentException("Unknown userId " + userId);

            StripeCustomer customer = StripeCustomer.from(zetaMaps.get(0));

            sql = "SELECT * FROM StripeSources WHERE UserId = ?";
            zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, userId);
            List<StripeSource> sources = new ArrayList<>();
            for (ZetaMap zetaMap : zetaMaps) {
                StripeSource source = StripeSource.from(zetaMap);
                sources.add(source);
            }

            customer.setSources(sources);
            return customer;
        }
    }

}
