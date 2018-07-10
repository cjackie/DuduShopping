ALTER PROCEDURE sp_ChatRoomUserJoin(
  @UserId BIGINT,
  @RoomId BIGINT
) AS

  DECLARE @ParticipantId BIGINT = 0
  DECLARE @JoinedAt DATETIME = SYSDATETIME()
  DECLARE @Error INT = 0

  SELECT @ParticipantId = ParticipantId, @JoinedAt = JoinedAt FROM ChatRoomParticipants WHERE RoomId = @RoomId AND UserId = @UserId AND Exited = 0
  IF @@ROWCOUNT <> 1 BEGIN
    -- user is not in the room. join the room now
    INSERT INTO ChatRoomParticipants(UserId, RoomId) VALUES (@UserId, @RoomId)
    SELECT @ParticipantId = @@IDENTITY, @Error = @@ERROR
  END

  SELECT @UserId AS UserId, @ParticipantId AS ParticipantId, @RoomId AS RoomId, @JoinedAt AS JoinedAt, @Error AS Error

