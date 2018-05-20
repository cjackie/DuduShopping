ALTER PROCEDURE sp_ShoppingOfferPull(
  @UserId BIGINT,
  @ShoppingOfferId BIGINT
) AS

DECLARE @Error INT = 0
DECLARE @State VARCHAR(5) = 'SO15'

SET NOCOUNT ON

SELECT @UserId = UserId FROM Users WHERE UserId = @UserId AND Role = 'S'
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

SELECT @ShoppingOfferId = ShoppingOfferId FROM ShoppingOffers WHERE ShoppingOfferId = @ShoppingOfferId AND State = 'SO5'
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
  GOTO ExitProc
END

UPDATE ShoppingOffers SET State = @State WHERE ShoppingOfferId = @ShoppingOfferId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 15
END


ExitProc:
  SELECT @Error AS Error

