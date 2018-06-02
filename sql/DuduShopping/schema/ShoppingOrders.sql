
-- OrderState:
-- OS5: order paid, OS10: item shipped, OS20: order done.
DROP TABLE ShoppingOrders
CREATE TABLE ShoppingOrders(
  OrderId BIGINT PRIMARY KEY IDENTITY,
  ShoppingRequestId BIGINT NOT NULL REFERENCES ShoppingRequests(ShoppingRequestId),
  ShoppingOfferId BIGINT NOT NULL REFERENCES ShoppingOffers(ShoppingOfferId),
  OrderState VARCHAR(5) NOT NULL,
)