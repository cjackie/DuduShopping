ALTER PROCEDURE sp_ShoppingRequestCancel(
  @UserId BIGINT,
  @ShoppingRequestId BIGINT
) AS

  DECLARE @Error INT = 0
  DECLARE @RequestState VARCHAR(5) = 'SR10'

  SET NOCOUNT ON

  BEGIN TRANSACTION

  SELECT @UserId = UserId FROM Users WHERE UserId = @UserId AND Role = 'C'
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 5
    GOTO ExitProc
  END

  SELECT @ShoppingRequestId = ShoppingRequestId FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId AND State IN ('SR5', 'SR15')
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 10
    GOTO ExitProc
  END

  UPDATE ShoppingRequests SET State = @RequestState WHERE ShoppingRequestId = @ShoppingRequestId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 15
    GOTO ExitProc
  END

ExitProc:

  IF @@TRANCOUNT <> 0 BEGIN
    IF @Error <> 0
      ROLLBACK
    ELSE
      COMMIT
  END

  SELECT @Error AS Error