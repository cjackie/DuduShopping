DROP TABLE StripeCustomers;
CREATE TABLE StripeCustomers (
  UserId BIGINT REFERENCES Users(UserId) UNIQUE NOT NULL,
  CustomerId VARCHAR (50) NOT NULL UNIQUE,
  -- indicate if an error happens, that needs to be taken care of. 0 means unlock.
  LockedReasonCode INT NOT NULL DEFAULT 0,
  CreatedAt DATETIME NOT NULL DEFAULT SYSDATETIME()
);