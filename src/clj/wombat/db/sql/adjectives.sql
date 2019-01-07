-- :name count-adjectives :? :1
SELECT COUNT(*) FROM adjectives;

-- :name get-adjective :? :1
SELECT * FROM adjectives WHERE id=:id LIMIT 1;

-- :name create-adjective! :i! :raw
INSERT INTO adjectives (word) VALUES (:word);
