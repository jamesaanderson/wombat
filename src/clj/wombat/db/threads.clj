(ns wombat.db.threads
  (:require [wombat.db.core :as db]))

(defn create! [thread]
  (db/create-thread! db/spec thread))

(defn get-by-room-id [room-id]
  (db/get-threads-by-room-id db/spec {:room-id room-id}))
