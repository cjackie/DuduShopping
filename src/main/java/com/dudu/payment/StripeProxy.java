package com.dudu.payment;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * interact with the stripe
 * Created by Chaojie (Jack) Wang on 5/17/18.
 */
/* package */ class StripeProxy {
    private static final Logger logger = LogManager.getLogger(StripeProxy.class);

    static StripeProxy getProxy() {
        return proxy;
    }

    private static final StripeProxy proxy = new StripeProxy();

    private StripeProxy() { }

    public static void configure(String apiKey) {
        Stripe.apiKey = apiKey;
    }

    /////////////////////////////////////////////

    /**
     *
     * @param description
     * @return Customer ID
     * @throws Exception
     */
    synchronized String createCustomer(String description) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("description", description);
        Customer customer = Customer.create(params);

        return customer.getId();
    }

    /**
     *
     * @param customerId
     * @param token
     * @return sourceId
     * @throws Exception
     */
    synchronized String addSource(String customerId, String token) throws Exception {
        Customer customer = Customer.retrieve(customerId);

        Map<String, Object> params = new HashMap<>();
        params.put("source", token);
        ExternalAccount account = customer.getSources().create(params);
        return account.getId();
    }

    /**
     * set payment method to a customer
     * @param customerId
     * @param sourceId
     * @throws Exception
     */
    void setDefaultPaymentMethod(String customerId, String sourceId) throws Exception {
        Customer customer = Customer.retrieve(customerId);

        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("default_source", sourceId);
        customer.update(updates);
    }

    /**
     *
     * @param customerId
     * @param amount cents. for example
     * @return charge ID
     */
    String charge(String customerId, long amount) throws Exception {
        final String currency = "usd";

        Map<String, Object> charge = new LinkedHashMap<>();
        charge.put("amount", amount);
        charge.put("currency", currency);
        charge.put("customer", customerId);

        Charge payment = Charge.create(charge);
        return payment.getId();
    }
}
