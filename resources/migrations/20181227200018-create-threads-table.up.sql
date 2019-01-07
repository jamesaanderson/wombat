CREATE TABLE IF NOT EXISTS threads (
  id bigserial PRIMARY KEY,
  subject varchar NOT NULL,
  body text NOT NULL,
  user_id bigserial NOT NULL,
  room_id bigserial NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USERS(id),
  FOREIGN KEY (room_id) REFERENCES ROOMS(id),
  created_at timestamp NOT NULL default current_timestamp
)
