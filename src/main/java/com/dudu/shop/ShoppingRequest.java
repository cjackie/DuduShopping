package com.dudu.shop;

import com.dudu.database.ZetaMap;

import java.util.Date;

/**
 * Created by chaojiewang on 5/13/18.
 */
public class ShoppingRequest {
    private long id;
    private long userId;
    private String text;
    private Date createdAt;
    private int State;

    public static ShoppingRequest from(ZetaMap zmap) {
        ShoppingRequest request = new ShoppingRequest();
        request.setId(zmap.getLong("Id"));
        request.setCreatedAt(zmap.getDate("CreatedAt"));
        request.setText(zmap.getString("Text"));
        request.setState(zmap.getInt("State"));
        return request;
    }

    ///////////////////////////////
    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getState() {
        return State;
    }

    void setState(int state) {
        State = state;
    }

    public long getUserId() {
        return userId;
    }

    void setUserId(long userId) {
        this.userId = userId;
    }
}
