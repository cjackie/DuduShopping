ALTER PROCEDURE sp_ShoppingRequestCancel(
  @ShoppingRequestId BIGINT
) AS

DECLARE @Error INT = 0
DECLARE @State VARCHAR(5) = 'SR10'

SELECT @ShoppingRequestId = ShoppingRequestId FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

UPDATE ShoppingRequests SET State = @State WHERE ShoppingRequestId = @ShoppingRequestId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
END

ExitProc:
  SELECT @Error AS Error