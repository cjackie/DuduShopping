DROP TABLE StripeCards
CREATE TABLE StripeCards(
  Id BIGINT PRIMARY KEY IDENTITY,
  UserId BIGINT REFERENCES Users(UserId),
  StripeCardSourceId VARCHAR(50) NOT NULL,
  DeletedOn DATETIME NULL,
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
)