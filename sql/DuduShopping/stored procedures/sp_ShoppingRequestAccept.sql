ALTER PROCEDURE sp_ShoppingRequestAccept(
  @UserId BIGINT,
  @ShoppingRequestId BIGINT,
  @ShoppingOfferId BIGINT
) AS

  DECLARE @Error INT = 0
  DECLARE @RequestState VARCHAR(5) = 'SR15'
  DECLARE @PrevState VARCHAR(5) = ''

  SET NOCOUNT ON

  BEGIN TRANSACTION

  SELECT @UserId = UserId FROM Users WHERE UserId = @UserId AND Role = 'C'
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 5
    GOTO ExitProc
  END

  SELECT @ShoppingRequestId = ShoppingRequestId, @PrevState = State FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId AND State = 'SR5'
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 10
    GOTO ExitProc
  END

  UPDATE ShoppingRequests SET State = @RequestState, ShoppingOfferAccepted = @ShoppingOfferId WHERE ShoppingRequestId = @ShoppingRequestId
  IF @Error <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 15
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


