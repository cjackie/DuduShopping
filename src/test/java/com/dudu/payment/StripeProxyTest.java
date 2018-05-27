package com.dudu.payment;

import com.dudu.common.TestBase;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by chaojiewang on 5/21/18.
 */
public class StripeProxyTest extends TestBase {
    boolean ready;

    @Before
    public void setup() {
        super.setup();

        if (dbReady) {
            String apiKey = System.getenv("API_KEY");
            if (apiKey != null) {
                StripeProxy.configure(apiKey);
                ready = true;
            }
        }
    }

    @Test
    public void createCustomer() throws Exception {
        Assume.assumeTrue(ready);

        String description = "user 2";
        String customerId = StripeProxy.getProxy().createCustomer(description);
        System.out.println(customerId);
    }

    @Test
    public void addSource() throws Exception {
        Assume.assumeTrue(ready);

        String customerId = "cus_Cw2z9SaL8jQ5df";
//        String token = "tok_visa";
        String token = "tok_visa_debit";

        String sourceId = StripeProxy.getProxy().addSource(customerId, token);
        System.out.println(sourceId);
    }

    @Test
    public void setDefaultPaymentMethod() throws Exception {
        Assume.assumeTrue(ready);

        String customerId = "cus_Cw2z9SaL8jQ5df";
        String sourceId = "card_1CWAzQGaN33MxmV1xRhqsl9Y";
        StripeProxy.getProxy().setDefaultPaymentMethod(customerId, sourceId);
    }


    @Test
    public void charge() throws Exception {
        Assume.assumeTrue(ready);

        String customerId = "cus_Cw2z9SaL8jQ5df";
        int amount = 100*100;
        String chargeId = StripeProxy.getProxy().charge(customerId, amount);
        println(chargeId);
    }
}
