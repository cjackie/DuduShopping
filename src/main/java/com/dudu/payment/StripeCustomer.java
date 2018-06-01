package com.dudu.payment;

import com.dudu.database.ZetaMap;

import java.util.Date;
import java.util.List;

/**
 * Created by chaojiewang on 5/27/18.
 */
public class StripeCustomer {
    private long userId;
    private String customerId;
    private int lockedReasonCode;
    private Date createdAt;
    private StripeSource defaultSource;
    private List<StripeSource> sources;

    public static StripeCustomer from(ZetaMap zetaMap) {
        StripeCustomer stripeCustomer = new StripeCustomer();
        stripeCustomer.userId = zetaMap.getLong("UserId");
        stripeCustomer.customerId = zetaMap.getString("CustomerId");
        stripeCustomer.lockedReasonCode = zetaMap.getInt("LockedReasonCode");
        stripeCustomer.createdAt = zetaMap.getDate("CreatedAt");

        return stripeCustomer;
    }

    ///////////////////////////////////////
    public long getUserId() {
        return userId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int isLockedReasonCode() {
        return lockedReasonCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public StripeSource getDefaultSource() {
        return defaultSource;
    }

    public List<StripeSource> getSources() {
        return sources;
    }

    public void setSources(List<StripeSource> sources) {
        this.sources = sources;

        if (sources != null) {
            for (StripeSource source : sources) {
                if (source.getUserId() == userId && source.isDefault())
                    defaultSource = source;
            }
        }
    }

    public boolean isLocked() {
        return lockedReasonCode != 0;
    }

    public int getLockedReasonCode() {
        return lockedReasonCode;
    }

}
