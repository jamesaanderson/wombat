CREATE TABLE IF NOT EXISTS rooms (
  id bigserial PRIMARY KEY,
  name varchar,
  created_at timestamp NOT NULL default current_timestamp
);
