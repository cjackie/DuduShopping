ALTER PROCEDURE sp_ShoppingRequestCreate
(
  @UserId BIGINT,
  @Text VARCHAR(300) = ''
) AS

DECLARE @State VARCHAR(5) = 'SR5'
DECLARE @Error INT = 0
DECLARE @ShoppingRequestId BIGINT = 0

SELECT * FROM Users WHERE UserId = @UserId AND Role = 'C'
IF @@ERROR <> 0  OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

INSERT INTO ShoppingRequests (UserId, Text, State) VALUES (@UserId, @Text, @State)
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
END
SET @ShoppingRequestId = @@IDENTITY

ExitProc:
  SELECT @Error AS Error, @ShoppingRequestId AS ShoppingRequestId
