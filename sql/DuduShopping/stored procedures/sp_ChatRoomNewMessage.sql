ALTER PROCEDURE sp_ChatRoomNewMessage(
  @PariticpantId BIGINT,
  @Message VARCHAR(500),
  @CreatedAt DATETIME
) AS

  INSERT INTO ChatRoomMessages(ParticipantId, Message, CreatedAt) VALUES (@PariticpantId, @Message, @CreatedAt)
  SELECT @@ERROR AS Error
