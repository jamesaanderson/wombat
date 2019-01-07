CREATE TABLE IF NOT EXISTS adjectives (
  id bigserial PRIMARY KEY,
  word varchar UNIQUE NOT NULL
);
