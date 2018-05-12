DROP TABLE Users;
CREATE TABLE Users (
  UserId BIGINT PRIMARY KEY IDENTITY,
  Login VARCHAR(20) NOT NULL,
  Password VARCHAR(100) NOT NULL,
  Role VARCHAR(1) NOT NULL,
  Scope VARCHAR(50) NOT NULL,
  CreatedOn DATETIME NOT NULL DEFAULT SYSDATETIME(),
  LastLogin DATETIME NULL,
  Address VARCHAR(100) NOT NULL DEFAULT '',
  UNIQUE (Login, Role)
)