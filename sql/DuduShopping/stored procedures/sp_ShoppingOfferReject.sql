ALTER PROCEDURE sp_ShoppingOfferReject (
  @UserId BIGINT, -- those who make a request, customer
  @ShoppingOfferId BIGINT
) AS

DECLARE @Error INT = 0
DECLARE @State VARCHAR(5) = 'SO20'

SET NOCOUNT ON

SELECT @ShoppingOfferId = ShoppingOfferId FROM ShoppingOffers o INNER JOIN ShoppingRequests r ON r.ShoppingRequestId = o.ShoppingRequestId INNER JOIN Users u ON u.UserId = r.UserId WHERE u.UserId = @UserId AND u.Role = 'C' AND ShoppingOfferId = @ShoppingOfferId AND o.State = 'SO5'
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 5
  GOTO ExitProc
END

UPDATE ShoppingOffers SET State = @State WHERE ShoppingOfferId = @ShoppingOfferId
IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
  SET @Error = 10
END

ExitProc:
  SELECT @Error AS Error

