ALTER PROCEDURE sp_UserLogin(
  @Login VARCHAR(20),
  @Password VARCHAR(100)
) AS

  DECLARE @Error INT = 0
  
  SET NOCOUNT ON
  
  BEGIN TRANSACTION
  
  SELECT @Login = Login FROM Users WHERE Login = @Login AND Password = @Password
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 5
    GOTO ExitProc
  END
  
  UPDATE Users SET LastLogin = SYSDATETIME() WHERE Login = @Login AND Password = @Password
  IF @@ERROR <> 0 OR @@ROWCOUNT <> 1 BEGIN
    SET @Error = 10
    GOTO ExitProc
  END
  
  
  INSERT INTO UserEvents (Login, EventCode, Description) VALUES (@Login, 100, 'Logon in successfully')
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


