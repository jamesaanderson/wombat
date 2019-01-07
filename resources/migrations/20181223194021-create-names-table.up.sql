CREATE TABLE IF NOT EXISTS names (
  id bigserial PRIMARY KEY,
  word varchar UNIQUE NOT NULL
);
