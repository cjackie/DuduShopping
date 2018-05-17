package com.dudu.payment;

import com.dudu.database.DBHelper;
import com.dudu.database.ZetaMap;
import com.stripe.Stripe;
import com.stripe.model.Customer;
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
    }

    /////////////////////////////////////////////

    /**
     *
     * @param userId
     * @return Customer ID
     * @throws Exception
     */
    synchronized public String createCustomer(String userId) throws Exception {
        Stripe.apiKey = apiKey;

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
    synchronized public String createCard(String userId) throws Exception {
        throw new NotImplementedException();
    }
}
