ALTER PROCEDURE sp_ShoppingOfferCreate(
  @UserId BIGINT,
  @Text VARCHAR(200),
  @Price DECIMAL(9,2),
  @ShoppingRequestId BIGINT
) AS

DECLARE @Error INT = 0
DECLARE @State VARCHAR(5) = 'SO5'
DECLARE @ShoppingOfferId BIGINT

SET NOCOUNT ON

SELECT @UserId = UserId FROM Users WHERE UserId = @UserId AND Role = 'S'
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

SELECT @ShoppingRequestId = ShoppingRequestId FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId AND State = 'SR5'
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
  GOTO ExitProc
END

INSERT INTO ShoppingOffers (UserId, ShoppingRequestId, Text, Price, State) VALUES (@UserId, @ShoppingRequestId, @Text, @Price, @State)
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 15
  GOTO ExitProc
END

SET @ShoppingOfferId = @@IDENTITY

ExitProc:
  SELECT @Error AS Error, @ShoppingOfferId AS ShoppingOfferId





