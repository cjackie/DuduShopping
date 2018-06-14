/**
 * Create an order that is in LIMBO state. it has no been paid yet.
 */
ALTER PROCEDURE sp_ShoppingOrderCreate_Limbo(
  @ShoppingRequestId BIGINT,
  @ShoppingOfferId BIGINT
) AS

  DECLARE @Error INT = 0
  DECLARE @OrderId BIGINT
  DECLARE @OrderState CHAR(5) = 'OS0'
  DECLARE @OfferState CHAR(5) = 'SO5'
  DECLARE @RequestState CHAR(5) = 'SR15'
  DECLARE @RequestText VARCHAR(200)
  DECLARE @RequestCreatedAt DATETIME
  DECLARE @OfferText VARCHAR(300)
  DECLARE @OfferPrice DECIMAL(9,2)
  DECLARE @OfferCreatedAt DATETIME

  BEGIN TRANSACTION

  SELECT @RequestText = Text, @RequestCreatedAt = CreatedAt FROM ShoppingRequests WHERE ShoppingRequestId = @ShoppingRequestId AND State = @RequestState AND ShoppingOfferAccepted = @ShoppingOfferId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 5
    GOTO ExitProc
  END

  SELECT @OfferText = Text, @OfferPrice = Price, @OfferCreatedAt = CreatedAt FROM ShoppingOffers WHERE ShoppingOfferId = @ShoppingOfferId AND State = @OfferState
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 10
    GOTO ExitProc
  END

  -- update state on request and offer
  UPDATE ShoppingOffers SET State = 'SO35' WHERE ShoppingOfferId = @ShoppingOfferId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 20
    GOTO ExitProc
  END

  UPDATE ShoppingRequests SET State = 'SR30' WHERE ShoppingRequestId = @ShoppingRequestId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 30
  END

  -- create new order
  INSERT INTO ShoppingOrders (RequestId, RequestText, RequestCreatedAt, OfferId, OfferText, OfferPrice, OfferCreatedAt, OrderState) VALUES (@ShoppingRequestId, @RequestText, @RequestCreatedAt, @ShoppingOfferId, @OfferText, @OfferPrice, @OfferCreatedAt, @OrderState)
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 15
    GOTO ExitProc
  END
  SET @OrderId = @@IDENTITY


ExitProc:

  IF @@TRANCOUNT <> 0 BEGIN
    IF @Error = 0
      COMMIT TRANSACTION
    ELSE
      ROLLBACK
  END

  SELECT @Error AS Error, @OrderId AS OrderId

