package com.dudu.payment;

import javax.sql.DataSource;

/**
 * Created by Chaojie (Jack) Wang on 5/17/18.
 */
public class StripeProcessor implements PaymentProcessor {
    private DataSource source;
    private String apiKey;

    public StripeProcessor(DataSource source, String apiKey) {
        this.source = source;
        this.apiKey = apiKey;
    }

    @Override
    public void charge(String cardId, double amt) throws Exception {

    }

}
