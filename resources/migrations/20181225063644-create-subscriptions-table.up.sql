CREATE TABLE IF NOT EXISTS subscriptions (
  id bigserial PRIMARY KEY,
  user_id bigserial NOT NULL,
  room_id bigserial NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USERS(id),
  FOREIGN KEY (room_id) REFERENCES ROOMS(id),
  UNIQUE (user_id, room_id),
  created_at timestamp NOT NULL default current_timestamp
);
