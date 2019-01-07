(ns wombat.db.names
  (:require [wombat.db.core :as db]))

(defn count-names []
  (:count
    (db/count-names db/spec)))

(defn get-name [id]
  (:word
    (db/get-name db/spec {:id id})))

(defn create! [word]
  (db/create-name! db/spec {:word word}))
