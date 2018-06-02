package com.dudu.payment;

import com.dudu.database.ZetaMap;

import java.util.Date;

public class StripeCharge {
    private long userId;
    private long orderId;
    private long amount;
    private String currency;
    private String stripeChargeToken;
    private int status;
    private Date chargedAt;

    public static StripeCharge from(ZetaMap zetaMap) {
        StripeCharge charge = new StripeCharge();
        charge.userId = zetaMap.getLong("UserId");
        charge.orderId = zetaMap.getLong("OrderId");
        charge.amount = zetaMap.getLong("Amount");
        charge.currency = zetaMap.getString("Currency");
        charge.stripeChargeToken = zetaMap.getString("StripeChargeToken");
        charge.status = zetaMap.getInt("Status");
        charge.chargedAt = zetaMap.getDate("ChargedAt");

        return charge;
    }

    ////////////////////////////////////
    public long getUserId() {
        return userId;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStripeChargeToken() {
        return stripeChargeToken;
    }

    public int getStatus() {
        return status;
    }

    public Date getChargedAt() {
        return chargedAt;
    }
}
