-- :name create-user! :i! :raw
INSERT INTO users (email, password) VALUES (:email, :password);

-- :name update-user-username! :i! :raw
UPDATE users SET username=:username WHERE id=:id;

-- :name get-user-by-email :? :1
SELECT * FROM users WHERE email=:email LIMIT 1;
