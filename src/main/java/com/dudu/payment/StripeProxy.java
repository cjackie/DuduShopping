package com.dudu.payment;

import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

/**
 * Created by Chaojie (Jack) Wang on 5/17/18.
 */
public class StripeProxy {
    private static final Logger logger = LogManager.getLogger(StripeProxy.class);
    private static String apiKey;
    private static DataSource source;

    public static StripeProxy getProxy() {
        return proxy;
    }

    private static final StripeProxy proxy = new StripeProxy();

    private StripeProxy() { }

    public static void configure(DataSource source, String apiKey) {
        proxy.apiKey = apiKey;
        proxy.source = source;
        Stripe.apiKey = apiKey;
    }

    /////////////////////////////////////////////

    /**
     *
     * @param userId
     * @return Customer ID
     * @throws Exception
     */
    synchronized public String createCustomer(String userId) throws Exception {
        // check if it has been created before
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM StripeCustomers WHERE UserId = ?";
            List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, sql, userId);
            if (zetaMaps.size() != 0)
                throw new IllegalArgumentException("customer has been created for UserId=" + userId);
        }

        // create one
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("description", "UserId=" + userId);
        Customer customer = Customer.create(params);
        try (Connection conn = source.getConnection()) {
            String sql = "INSERT INTO StripeCustomers (UserId, CustomerId) VALUES (?, ?)";
            int c = DBHelper.getHelper().execUpdate(conn, sql, userId, customer.getId());
            if (c != 1) {
                String error = "Failed to insert a customer to our database. CustomerId=" + customer.getId() + ", UserId=" + userId;
                logger.error(error);
                throw new IllegalStateException(error);
            }
        }

        return customer.getId();
    }

    /**
     *
     * @param userId
     * @return
     * @throws Exception
     */
    synchronized public String createCard(String userId, Card card) throws Exception {
        Customer customer = retrieveCustomer(userId);
        Map<String, Object> cardMap = new LinkedHashMap<>();
        cardMap.put("number", card.getNumber());
        cardMap.put("exp_month", card.getExpMonth());
        cardMap.put("exp_year", card.getExpYear());
        cardMap.put("cvc", card.getCvc());
        ExternalAccount cardSaved = customer.getSources().create(cardMap);
        return cardSaved.getId();
    }

    /**
     * set payment method to a user
     * @param userId
     * @param stripeCardId
     * @throws Exception
     */
    public void setPaymentMethod(String userId, String stripeCardId) throws Exception {
        String sourceId;
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT StripeCardSourceId FROM StripeCards WHERE UserId = ? AND Id = ?";
            ZetaMap zmap = DBHelper.getHelper().execToZetaMaps(conn, sql, userId, stripeCardId).get(0);
            sourceId = String.valueOf(zmap.getLong("StripeCardSourceId"));
        }

        Customer customer = retrieveCustomer(userId);
        ExternalAccount externalAccount = customer.getSources().retrieve(sourceId);
        if (externalAccount == null)
            throw new IllegalArgumentException("Invalid sourceId");

        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("source", sourceId);
        customer.update(updates);
    }

    private Customer retrieveCustomer(String userId) throws Exception {
        try (Connection conn = source.getConnection()) {
            Customer customer;
            String sql = "SELECT * FROM StripeCustomers WHERE UserId = ?";
            ZetaMap zetaMap = DBHelper.getHelper().execToZetaMaps(conn, sql, userId).get(0);
            customer = Customer.retrieve(zetaMap.getString("CustomerId"));
            return customer;
        }
    }

    public class Card {
        private String number;
        private int expMonth;
        private int expYear;
        private int cvc;

        public Card(String number, int expMonth, int expYear, int cvc) {
            this.number = number;
            this.expMonth = expMonth;
            this.expYear = expYear;
            this.cvc = cvc;
        }

        public String getNumber() {
            return number;
        }

        public int getExpMonth() {
            return expMonth;
        }

        public int getExpYear() {
            return expYear;
        }

        public int getCvc() {
            return cvc;
        }
    }
}
