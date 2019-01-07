CREATE TABLE IF NOT EXISTS users (
  id bigserial PRIMARY KEY,
  email varchar UNIQUE,
  username varchar UNIQUE,
  password varchar NOT NULL,
  created_at timestamp NOT NULL default current_timestamp
);
