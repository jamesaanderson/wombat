(ns wombat.db.subscriptions
  (:require [wombat.db.core :as db]))

(defn create!
  [subscription]
  (db/create-subscription! db/spec subscription))

(defn get-by-user-id
  [user-id]
  (db/get-subscriptions-by-user-id db/spec {:user-id user-id}))
