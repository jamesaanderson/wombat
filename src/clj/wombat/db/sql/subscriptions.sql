-- :name create-subscription! :i! :raw
INSERT INTO subscriptions (user_id, room_id) VALUES (:user-id, :room-id);

-- :name get-subscriptions-by-user-id :? :raw
SELECT * FROM rooms r INNER JOIN subscriptions s ON r.id=s.room_id WHERE user_id=:user-id;
