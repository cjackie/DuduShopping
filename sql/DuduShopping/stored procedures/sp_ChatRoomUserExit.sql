ALTER PROCEDURE sp_ChatRoomUserExit(
  @ParticipantId BIGINT
) AS

  UPDATE ChatRoomParticipants SET Exited = 1 WHERE ParticipantId = @ParticipantId
  SELECT @@ERROR AS Error
