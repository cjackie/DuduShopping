
-- Offers from sale agents
-- state:
-- offer = SO5, sale agent pulled = SO15
--    shopper reject it = SO20
DROP TABLE ShoppingOffers;
CREATE TABLE ShoppingOffers (
  UserId BIGINT REFERENCES Users(UserId),
  ShoppingOfferId BIGINT PRIMARY KEY IDENTITY,
  ShoppingRequestId BIGINT REFERENCES ShoppingRequests(ShoppingRequestId) NULL,
  Text VARCHAR(200) NOT NULL DEFAULT '',
  Price DECIMAL(9, 2) NOT NULL,
  State VARCHAR(5) NOT NULL,
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
)
