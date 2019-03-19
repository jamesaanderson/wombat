-- :name create-reply! :i! :raw
INSERT INTO replies (body, user_id, thread_id, parent_id) VALUES (:body, :user-id, :thread-id, :parent-id);

-- :name get-replies-by-thread-id :? :raw
SELECT r.*,u.username FROM replies r LEFT JOIN users u ON r.user_id=u.id WHERE thread_id=:thread-id;

-- :name get-replies-by-thread-id-limit :? :raw
SELECT * FROM
  (SELECT r.*,u.username FROM replies r LEFT JOIN users u ON r.user_id=u.id WHERE thread_id=:thread-id ORDER BY r.id DESC LIMIT :limit) AS recent
ORDER BY id ASC;
