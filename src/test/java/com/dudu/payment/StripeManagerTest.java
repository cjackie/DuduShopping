package com.dudu.payment;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class StripeManagerTest extends TestBase {
    boolean ready;
    StripeManager manager;

    @Before
    @Override
    public void setup() {
        super.setup();

        if (dbReady) {
            DataSource source = DBManager.getManager().getDataSource("DuduShopping");

            String apiKey = null;
            try (InputStream in = new FileInputStream("./conf/StripeApiKey.conf")) {
                Properties props = new Properties();
                props.load(in);
                apiKey = props.getProperty("API_KEY");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (source != null && apiKey != null) {
                StripeProxy.configure(apiKey);
                manager = new StripeManager(source);
                ready = true;
            }
        }
    }

    @Test
    public void createCustomer() throws Exception {
        Assume.assumeTrue(ready);

        long userId = 1;
        String customerId = manager.createCustomer(userId);
        println(customerId);
    }

    @Test
    public void addSource() throws Exception {
        Assume.assumeTrue(ready);

        long userId = 1;
        String token = "tok_visa_debit";
        String last4 = "4242";
        int expMonth = 4;
        int expYear = 2020;
        String funding = "";
        String brand = "";

        manager.addSource(userId, token, last4, expMonth, expYear, funding, brand);
        println("done");
    }

//    @Test
//    public void isLocked() throws Exception {
//        Assume.assumeTrue(ready);
//
//        long userId = 1;
//        println(manager.isLocked(userId));
//    }


//    @Test
//    public void lock() throws Exception {
//        Assume.assumeTrue(ready);
//
//        long userId = 1;
//        int reasonCode = 100;
//        manager.lock(userId, reasonCode);
//        println("done");
//    }

    @Test
    public void getCustomer() throws Exception {
        Assume.assumeTrue(ready);

        long userId = 1;
        StripeCustomer customer = manager.getCustomer(userId);
        println(customer.getUserId());
    }


    @Test
    public void setPaymentMethod() throws Exception {
        Assume.assumeTrue(ready);

        long userId = 1;
        String sourceId = "card_1CYZxSGaN33MxmV17y5zdm4g";
        manager.setPaymentMethod(userId, sourceId);
        println("done");
    }

    @Test
    public void charge() throws Exception {
        long userId = 1;
        long orderId = 1;
        long amount = 1000;

        String token = manager.charge(orderId, userId, amount);
        println(token);
    }

    @Test
    public void getCharge() throws Exception {
        long userId = 1;
        String stripeChargeToken = "ch_1CYhhPGaN33MxmV1O279hVoK";

        StripeCharge charge = manager.getCharge(userId, stripeChargeToken);
        println(charge.getUserId());
    }

}
