-- :name count-names :? :1
SELECT COUNT(*) FROM names;

-- :name get-name :? :1
SELECT * FROM names WHERE id=:id LIMIT 1;

-- :name create-name! :i! :raw
INSERT INTO names (word) VALUES (:word);
