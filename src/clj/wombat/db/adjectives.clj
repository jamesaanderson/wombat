(ns wombat.db.adjectives
  (:require [wombat.db.core :as db]))

(defn count-adjectives []
  (:count
    (db/count-adjectives db/spec)))

(defn get-adjective [id]
  (:word
    (db/get-adjective db/spec {:id id})))

(defn create! [word]
  (db/create-adjective! db/spec {:word word}))
