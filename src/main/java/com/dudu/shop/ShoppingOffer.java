package com.dudu.shop;

import com.dudu.database.ZetaMap;

import java.util.Date;

/**
 * Created by chaojiewang on 5/17/18.
 */
public class ShoppingOffer {
    private long shoppingOfferId;
    private long shoppingRequestId;
    private String text;
    private double price;
    private String state;
    private Date createdAt;

    public static ShoppingOffer from(ZetaMap zmap) {
        ShoppingOffer offer = new ShoppingOffer();
        offer.shoppingOfferId = zmap.getLong("ShoppingOfferId");
        offer.shoppingRequestId = zmap.getLong("ShoppingRequestId");
        offer.text = zmap.getString("Text");
        offer.price = zmap.getDouble("Price");
        offer.state = zmap.getString("State");
        offer.createdAt = zmap.getDate("CreatedAt");

        return offer;
    }

    ///////////////////////////////////////////////////////////////////
    public long getShoppingOfferId() {
        return shoppingOfferId;
    }

    public void setShoppingOfferId(long shoppingOfferId) {
        this.shoppingOfferId = shoppingOfferId;
    }

    public long getShoppingRequestId() {
        return shoppingRequestId;
    }

    public void setShoppingRequestId(long shoppingRequestId) {
        this.shoppingRequestId = shoppingRequestId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
