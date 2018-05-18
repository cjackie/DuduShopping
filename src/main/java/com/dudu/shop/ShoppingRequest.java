package com.dudu.shop;

import com.dudu.database.ZetaMap;

import java.util.Date;

/**
 * Created by chaojiewang on 5/13/18.
 */
public class ShoppingRequest {
    private long shoppingRequestId;
    private long userId;
    private String text;
    private Date createdAt;
    private String state;

    public static ShoppingRequest from(ZetaMap zmap) {
        ShoppingRequest request = new ShoppingRequest();
        request.setShoppingRequestId(zmap.getLong("ShoppingRequestId"));
        request.setUserId(zmap.getLong("UserId"));
        request.setText(zmap.getString("Text"));
        request.setCreatedAt(zmap.getDate("CreatedAt"));
        request.setState(zmap.getString("State"));
        return request;
    }

    ///////////////////////////////
    public long getShoppingRequestId() {
        return shoppingRequestId;
    }

    public void setShoppingRequestId(long shoppingRequestId) {
        this.shoppingRequestId = shoppingRequestId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
