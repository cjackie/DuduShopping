ALTER PROCEDURE sp_ChatRoomCreate(
  @ChatRoomName VARCHAR(50)
) AS

  INSERT INTO ChatRooms(Name) VALUES (@ChatRoomName)
  SELECT @@IDENTITY AS RoomId

