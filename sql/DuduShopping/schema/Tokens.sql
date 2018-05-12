DROP TABLE Tokens;
CREATE TABLE Tokens (
  UserId VARCHAR(20),
  Id INT PRIMARY KEY IDENTITY,
  Token VARCHAR(80) NOT NULL,
  RefreshToken VARCHAR(80) NOT NULL,
  IssueAt DATETIME NOT NULL,
  ExpireIn INT NOT NULL, -- seconds, usually 3600
  Scope VARCHAR(100) NULL, -- comma seperated values
  IsValid BIT NOT NULL DEFAULT 1
)


