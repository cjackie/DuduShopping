DROP TABLE StripeCustomers;
CREATE TABLE StripeCustomers (
  UserId BIGINT REFERENCES Users(UserId) UNIQUE NOT NULL,
  CustomerId VARCHAR (50) NOT NULL,
  DefaultCard BIGINT REFERENCES StripeCards(Id) NULL,
  CardProvidedAt DATETIME NULL, -- NULL means no card provided
  InvalidSince DATETIME NULL, -- NULL means is invalid
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
);