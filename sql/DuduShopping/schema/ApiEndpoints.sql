DROP TABLE ApiEndpoints;
CREATE TABLE ApiEndpoints
(
  Enpoint VARCHAR(100) NOT NULL,
  Method VARCHAR(5) NOT NULL,
  Scope VARCHAR(20) NOT NULL,
  UNIQUE (Enpoint, Method)
)