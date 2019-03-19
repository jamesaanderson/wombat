(ns wombat.db.subscriptions
  (:require [wombat.db.core :as db]))

(defn create! [subscription]
  (try
    (-> (db/create-subscription! db/spec subscription)
        (assoc :success? true))
    (catch Exception e
      {:success? false :message "Error creating subscription."})))

(defn get-by-user-id [user-id]
  (db/get-subscriptions-by-user-id db/spec {:user-id user-id}))
