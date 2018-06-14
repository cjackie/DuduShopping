-- OrderState:
-- OS0: limbo,
-- OS5: order paid, OS10: item shipped, OS20: order done.
DROP TABLE ShoppingOrders
CREATE TABLE ShoppingOrders(
  OrderId BIGINT PRIMARY KEY IDENTITY,
  RequestId BIGINT NOT NULL REFERENCES ShoppingRequests(ShoppingRequestId),
  RequestText VARCHAR(200) NOT NULL DEFAULT '',
  RequestCreatedAt DATETIME NOT NULL,
  OfferId BIGINT NOT NULL REFERENCES ShoppingOffers(ShoppingOfferId),
  OfferText VARCHAR(300) NOT NULL DEFAULT '',
  OfferPrice DECIMAL(9,2) NOT NULL,
  OfferCreatedAt DATETIME NOT NULL,
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME(),
  OrderState VARCHAR(5) NOT NULL,
  ShipmentTrackingNumber VARCHAR(30) NULL,
)