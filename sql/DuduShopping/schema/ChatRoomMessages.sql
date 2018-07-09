CREATE TABLE ChatRoomMessages(
  MessageId BIGINT NOT NULL PRIMARY KEY IDENTITY,
  ParticipantId BIGINT REFERENCES ChatRoomParticipants(ParticipantId),
  Message VARCHAR(500) NOT NULL DEFAULT '',
  CreatedAt DATETIME DEFAULT SYSDATETIME()
)