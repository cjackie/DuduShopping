package com.dudu.shop;

import com.dudu.database.ZetaMap;

public class ShoppingOrder {
    private long orderId;
    private long shoppingRequestId;
    private long shoppingOfferId;
    private String orderState;

    public static ShoppingOrder from(ZetaMap zetaMap) {
        ShoppingOrder order = new ShoppingOrder();
        order.orderId = zetaMap.getLong("OrderId");
        order.shoppingRequestId = zetaMap.getLong("ShoppingRequestId");
        order.shoppingOfferId = zetaMap.getLong("ShoppingOfferId");
        order.orderState = zetaMap.getString("OrderState");

        return order;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getShoppingRequestId() {
        return shoppingRequestId;
    }

    public long getShoppingOfferId() {
        return shoppingOfferId;
    }

    public String getOrderState() {
        return orderState;
    }
}
