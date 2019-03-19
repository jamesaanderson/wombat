-- :name create-thread! :i! :raw
INSERT INTO threads (subject, body, user_id, room_id) VALUES (:subject, :body, :user-id, :room-id);

-- :name get-threads-by-room-id :? :raw
SELECT t.*,u.username FROM threads t LEFT JOIN users u ON t.user_id=u.id WHERE room_id=:room-id;

-- :name get-threads-by-id :? :1
SELECT t.*,u.username FROM threads t LEFT JOIN users u ON t.user_id=u.id WHERE t.id=:id;

-- :name get-feed :? :raw
SELECT
  t.*,
  u.username,
  r.name room_name
FROM threads t
INNER JOIN subscriptions s ON t.room_id=s.room_id
LEFT JOIN users u ON t.user_id=u.id
LEFT JOIN rooms r ON t.room_id=r.id
WHERE s.user_id=:user-id;
