CREATE TABLE IF NOT EXISTS replies (
  id bigserial PRIMARY KEY,
  body text NOT NULL,
  user_id bigint NOT NULL,
  thread_id bigint NOT NULL,
  parent_id bigint,
  FOREIGN KEY (user_id) REFERENCES USERS(id),
  FOREIGN KEY (thread_id) REFERENCES THREADS(id),
  FOREIGN KEY (parent_id) REFERENCES REPLIES(id),
  created_at timestamp NOT NULL default current_timestamp
);
