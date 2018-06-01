package com.dudu.payment;

import com.dudu.database.ZetaMap;

import java.util.Date;
import java.util.Objects;

public class StripeSource {
    private long userId;
    private String sourceId;
    private boolean isDefault;
    private Date createdAt;
    private String lastFour;
    private int expMonth;
    private int expYear;
    private String funding;
    private String brand;

    public static StripeSource from(ZetaMap zetaMap) {
        StripeSource stripeSource = new StripeSource();
        stripeSource.userId = zetaMap.getLong("UserId");
        stripeSource.sourceId = zetaMap.getString("SourceId");
        stripeSource.isDefault = zetaMap.getInt("IsDefault") == 1;
        stripeSource.createdAt = zetaMap.getDate("CreatedAt");
        stripeSource.lastFour = zetaMap.getString("LastFour");
        stripeSource.expMonth = zetaMap.getInt("ExpMonth");
        stripeSource.expYear = zetaMap.getInt("ExpYear");
        stripeSource.funding = zetaMap.getString("Funding");
        stripeSource.brand = zetaMap.getString("Brand");

        return stripeSource;
    }

    ///////////////////////////////////////////
    public long getUserId() {
        return userId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getLastFour() {
        return lastFour;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public String getFunding() {
        return funding;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StripeSource that = (StripeSource) o;
        return Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(sourceId);
    }
}
