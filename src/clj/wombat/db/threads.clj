(ns wombat.db.threads
  (:require [wombat.db.core :as db]))

(defn create! [thread]
  (db/create-thread! db/spec thread))
