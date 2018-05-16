ALTER PROCEDURE sp_ShoppingRequestAccept(
  @ShoppingRequestId BIGINT,
  @ShoppingOfferId BIGINT
) AS

DECLARE @Error INT = 0
DECLARE @RequestState VARCHAR(5) = 'SR15'
DECLARE @OfferState VARCHAR(5) = 'SO25'

BEGIN TRANSACTION

SELECT @ShoppingRequestId = ShoppingRequestId FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

SELECT @ShoppingOfferId = ShoppingOfferId FROM ShoppingOffers WHERE ShoppingOfferId = @ShoppingOfferId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
  GOTO ExitProc
END

UPDATE ShoppingRequests SET State = @RequestState WHERE ShoppingRequestId = @ShoppingRequestId
IF @Error <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 15
  GOTO ExitProc
END

UPDATE ShoppingOffers SET State = @OfferState WHERE ShoppingOfferId = @ShoppingOfferId
IF @Error <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 20
  GOTO ExitProc
END

ExitProc:
  IF @@TRANCOUNT = 1 BEGIN
    IF @Error = 0
      COMMIT
    ELSE
      ROLLBACK
  END

  SELECT @Error AS Error


