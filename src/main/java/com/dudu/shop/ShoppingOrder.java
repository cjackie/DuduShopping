package com.dudu.shop;
import com.dudu.database.ZetaMap;

import java.util.Date;

public class ShoppingOrder {
    private long orderId;
    private long requestId;
    private String requestText;
    private Date requestCreatedAt;
    private long offerId;
    private String offerText;
    private double offerPrice;
    private Date offerCreatedAt;
    private Date createdAt;
    private String orderState;
    private String shipmentTrackingNumber;

    public static ShoppingOrder from(ZetaMap zetaMap) {
        ShoppingOrder order = new ShoppingOrder();
        order.orderId = zetaMap.getLong("OrderId");
        order.requestId = zetaMap.getLong("RequestId");
        order.requestText = zetaMap.getString("RequestText");
        order.requestCreatedAt = zetaMap.getDate("RequestCreatedAt");

        order.offerId = zetaMap.getLong("OfferId");
        order.offerText = zetaMap.getString("OfferText");
        order.offerCreatedAt = zetaMap.getDate("OfferCreatedAt");
        order.offerPrice = zetaMap.getDouble("OfferPrice");

        order.createdAt = zetaMap.getDate("CreatedAt");
        order.orderState = zetaMap.getString("OrderState");

        order.shipmentTrackingNumber = zetaMap.getString("ShipmentTrackingNumber");

        return order;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getRequestId() {
        return requestId;
    }

    public String getRequestText() {
        return requestText;
    }

    public Date getRequestCreatedAt() {
        return requestCreatedAt;
    }

    public long getOfferId() {
        return offerId;
    }

    public String getOfferText() {
        return offerText;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public Date getOfferCreatedAt() {
        return offerCreatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getOrderState() {
        return orderState;
    }

    public String getShipmentTrackingNumber() {
        return shipmentTrackingNumber;
    }
}
