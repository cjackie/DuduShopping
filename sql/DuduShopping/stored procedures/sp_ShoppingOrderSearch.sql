ALTER PROCEDURE sp_ShoppingOrderSearch(
  @UserId BIGINT,
  @Begin DATETIME = NULL,
  @End DATETIME = NULL
) AS

  DECLARE @Role CHAR(1)
  DECLARE @Error INT = 0

  SELECT @Role = Role FROM Users WHERE UserId = @UserId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 5
    GOTO ErrorExit
  END

  IF @Role = 'C'
    SELECT o.* FROM ShoppingOrders o INNER JOIN ShoppingRequests r ON r.ShoppingRequestId = o.RequestId WHERE r.UserId = @UserId AND (@Begin IS NULL OR o.CreatedAt >= @Begin) AND (@End IS NULL OR o.CreatedAt <= @End)
  ELSE IF @Role = 'S'
    SELECT o.* FROM ShoppingOrders o INNER JOIN ShoppingOffers so ON so.ShoppingOfferId = o.OfferId WHERE so.UserId = @UserId AND (@Begin IS NULL OR o.CreatedAt >= @Begin) AND (@End IS NULL OR o.CreatedAt <= @End)

ErrorExit:
  -- do nothing






