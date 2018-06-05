ALTER PROCEDURE sp_ShoppingMatchQuery(
  @UserId BIGINT = 0
) AS

  SET NOCOUNT ON

  DECLARE @UserRole CHAR(1)
  DECLARE @ShoppingRequestState VARCHAR(5) = 'SR15'
  DECLARE @ShoppingOfferState VARCHAR(5) = 'SO5'

  CREATE TABLE #MatchTmp (
    UserId BIGINT,

    ShoppingRequestId BIGINT,
    ShoppingRequestState VARCHAR(5), -- always SR15
    ShoppingRequestText VARCHAR(300),
    ShoppingRequestCreatedAt DATETIME,

    ShoppingOfferId BIGINT,
    ShoppingOfferState VARCHAR(5),
    ShoppingOfferText VARCHAR(200),
    ShoppingOfferPrice DECIMAL(9,2),
    ShoppingOfferCreatedAt DATETIME
  )

  SELECT @UserRole = Role FROM Users WHERE UserId = @UserId
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1
      GOTO ExitProc

  IF @UserRole = 'C' BEGIN

    INSERT INTO #MatchTmp
    SELECT
      sr.UserId, sr.ShoppingRequestId, sr.State, sr.Text, sr.CreatedAt,
      so.ShoppingOfferId, so.State, so.Text, so.Price, so.CreatedAt
    FROM ShoppingRequests sr
      INNER JOIN ShoppingOffers so ON so.ShoppingRequestId = sr.ShoppingRequestId
    WHERE sr.UserId = @UserId AND sr.State = @ShoppingRequestState AND sr.ShoppingOfferAccepted = so.ShoppingOfferId

  END ELSE IF @UserRole = 'S' BEGIN

    INSERT INTO #MatchTmp
    SELECT
      sr.UserId, sr.ShoppingRequestId, sr.State, sr.Text, sr.CreatedAt,
      so.ShoppingOfferId, so.State, so.Text, so.Price, so.CreatedAt
    FROM ShoppingRequests sr
      INNER JOIN ShoppingOffers so ON so.ShoppingRequestId = sr.ShoppingRequestId
    WHERE so.UserId = @UserId AND so.State = @ShoppingOfferState AND sr.ShoppingOfferAccepted = so.ShoppingOfferId

  END


ExitProc:
  SELECT * FROM #MatchTmp
  DROP TABLE #MatchTmp