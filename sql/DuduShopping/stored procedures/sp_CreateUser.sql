ALTER PROCEDURE sp_CreateUser (
  @Login VARCHAR(20),
  @Password VARCHAR(100),
  @Role CHAR(1),
  @Scope VARCHAR(50),
  @Address VARCHAR(100)
) AS

DECLARE @Error INT = 0
DECLARE @UserId BIGINT

SET NOCOUNT ON

-- check on arguments
IF @Role NOT IN ('C', 'S') OR @Scope IS NULL BEGIN
  SET @Error = 5
  GOTO ExitProc
END

SELECT @Login = Login FROM Users WHERE Login = @Login AND Role = @Role
IF @@ERROR <> 0 OR @@ROWCOUNT = 1 BEGIN
  SET @Error = 10
  GOTO ExitProc
END

INSERT INTO Users (Login, Password, Role, Scope, Address) VALUES (@Login, @Password, @Role, @Scope, @Address)
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 15
  GOTO ExitProc
END
SET @UserId = @@IDENTITY


ExitProc:
  SELECT @Error AS Error, @UserId AS UserId

