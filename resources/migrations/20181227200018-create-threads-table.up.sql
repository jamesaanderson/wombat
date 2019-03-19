CREATE TABLE IF NOT EXISTS threads (
  id bigserial PRIMARY KEY,
  subject varchar,
  body text NOT NULL,
  user_id bigint NOT NULL,
  room_id bigint NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USERS(id),
  FOREIGN KEY (room_id) REFERENCES ROOMS(id),
  created_at timestamp NOT NULL default current_timestamp
)
