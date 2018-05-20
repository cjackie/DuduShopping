
-- Requests made by customers
-- states:
-- placed = SR5, cancelled = SR10, accepted = SR15
DROP TABLE ShoppingRequests
CREATE TABLE ShoppingRequests(
  ShoppingRequestId BIGINT PRIMARY KEY IDENTITY,
  ShoppingOfferAccepted BIGINT NULL,
  UserId BIGINT REFERENCES Users(UserId),
  Text VARCHAR(300) NOT NULL DEFAULT '',
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME(),
  State CHAR(5) NOT NULL DEFAULT '',
)
