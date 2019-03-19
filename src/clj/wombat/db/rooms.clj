(ns wombat.db.rooms
  (:require [wombat.db.core :as db]
            [wombat.db.subscriptions :as subscriptions]
            [jkkramer.verily :as v]))

(def validations [[:required [:name]]])

(defn create! [m]
  (let [errors (v/validate m validations)
        valid? (or (empty? errors) (nil? errors))]
    (if valid?
      (try
        (let [room (db/create-room! db/spec m)
              subscription (subscriptions/create! {:user-id (:user-id m)
                                                   :room-id (:id room)})]
          (assoc room :success? true))
        (catch Exception _
          {:success? false :message "Error creating room."}))
      {:success? false :message errors})))
