CREATE TABLE ChatRoomParticipants(
  ParticipantId BIGINT NOT NULL IDENTITY PRIMARY KEY,
  UserId BIGINT NOT NULL REFERENCES Users(UserId),
  RoomId BIGINT NOT NULL REFERENCES ChatRooms(RoomId),
  JoinedAt DATETIME NOT NULL DEFAULT SYSDATETIME(),
  -- Default 0 means the participant is in the room, otherwise he/she exited
  Exited BIT NOT NULL DEFAULT 0,
)