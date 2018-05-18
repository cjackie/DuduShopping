DROP TABLE Tokens;
CREATE TABLE Tokens (
  UserId VARCHAR(20),
  Id BIGINT PRIMARY KEY IDENTITY,
  Token VARCHAR(80) NOT NULL,
  RefreshToken VARCHAR(80) NOT NULL,
  IssuedAt DATETIME NOT NULL,
  ExpiresIn INT NOT NULL, -- seconds, usually 3600
  Scope VARCHAR(100) NULL, -- comma seperated values
  IsValid BIT NOT NULL DEFAULT 1
)


