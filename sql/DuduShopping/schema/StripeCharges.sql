
DROP TABLE StripeCharges;
CREATE TABLE StripeCharges(
  UserId BIGINT REFERENCES Users(UserId),
  OrderId BIGINT REFERENCES ShoppingOrders(OrderId),
  -- multiple of lowest face value. for example, usd, it will be number of cents.
  Amount BIGINT NOT NULL DEFAULT 0,
  Currency VARCHAR(5) NOT NULL DEFAULT 'USD',
  StripeChargeToken VARCHAR(50) NOT NULL,
  Status INT NOT NULL DEFAULT 0, -- TODO Do i need this
  ChargedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
   )