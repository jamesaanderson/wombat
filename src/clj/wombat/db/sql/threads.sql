-- :name create-thread! :i! :raw
INSERT INTO threads (subject, body, user_id, room_id) VALUES (:subject, :body, :user-id, :room-id);

-- :name get-threads-by-room-id :? :raw
SELECT * FROM threads WHERE room_id=:room-id;
