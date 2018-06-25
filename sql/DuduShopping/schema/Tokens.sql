DROP TABLE Tokens;
CREATE TABLE Tokens (
  UserId BIGINT,
  Id BIGINT PRIMARY KEY IDENTITY,
  Token VARCHAR(80) NOT NULL UNIQUE,
  RefreshToken VARCHAR(80) NOT NULL UNIQUE,
  IssuedAt DATETIME NOT NULL,
  ExpiresIn INT NOT NULL, -- seconds, usually 3600
  Scopes VARCHAR(100) NULL, -- comma seperated values
  IsValid BIT NOT NULL DEFAULT 1
)


