

CREATE TABLE StripeCharges(
  UserId BIGINT REFERENCES Users(UserId),
  OrderId BIGINT REFERENCES ShoppingOrders(OrderId),
  StripeChargeToken VARCHAR(50) NOT NULL,
  Status INT NOT NULL DEFAULT 0, -- TODO Do i need this
  ChargedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
)