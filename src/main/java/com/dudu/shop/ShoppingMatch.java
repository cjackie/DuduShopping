package com.dudu.shop;

import com.dudu.database.ZetaMap;

import java.util.Date;

public class ShoppingMatch {
    private long userId;
    private long shoppingRequestId;
    private String shoppingRequestState;
    private String shoppingRequestText;
    private Date shoppingRequestCreatedAt;
    private long shoppingOfferId;
    private String shoppingOfferState;
    private String shoppingOfferText;
    private double shoppingOfferPrice;
    private Date shoppingOfferCreatedAt;

    public static ShoppingMatch from(ZetaMap zetaMap) {
        ShoppingMatch match = new ShoppingMatch();
        match.userId = zetaMap.getLong("UserId");
        match.shoppingRequestId = zetaMap.getLong("ShoppingRequestId");
        match.shoppingRequestState = zetaMap.getString("ShoppingRequestState");
        match.shoppingRequestText = zetaMap.getString("ShoppingRequestText");
        match.shoppingRequestCreatedAt = zetaMap.getDate("ShoppingRequestCreatedAt");

        match.shoppingOfferId = zetaMap.getLong("ShoppingOfferId");
        match.shoppingOfferState = zetaMap.getString("ShoppingOfferState");
        match.shoppingOfferText = zetaMap.getString("ShoppingOfferText");
        match.shoppingOfferPrice = zetaMap.getDouble("ShoppingOfferPrice");
        match.shoppingOfferCreatedAt = zetaMap.getDate("ShoppingOfferCreatedAt");

        return match;
    }

    ///////////////////////////////////////////////
    public long getUserId() {
        return userId;
    }

    public long getShoppingRequestId() {
        return shoppingRequestId;
    }

    public String getShoppingRequestState() {
        return shoppingRequestState;
    }

    public String getShoppingRequestText() {
        return shoppingRequestText;
    }

    public Date getShoppingRequestCreatedAt() {
        return shoppingRequestCreatedAt;
    }

    public long getShoppingOfferId() {
        return shoppingOfferId;
    }

    public String getShoppingOfferState() {
        return shoppingOfferState;
    }

    public String getShoppingOfferText() {
        return shoppingOfferText;
    }

    public double getShoppingOfferPrice() {
        return shoppingOfferPrice;
    }

    public Date getShoppingOfferCreatedAt() {
        return shoppingOfferCreatedAt;
    }
}
