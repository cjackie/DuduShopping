DROP TABLE UserEvents;
CREATE TABLE UserEvents(
  Login VARCHAR(20) NOT NULL,
  Role CHAR(1) NOT NULL,
  EventCode INT NOT NULL DEFAULT 0,
  Description VARCHAR(200) NOT NULL DEFAULT '',
  CreatedOn DATETIME DEFAULT SYSDATETIME()
)