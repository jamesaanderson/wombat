(ns wombat.db.rooms
  (:require [wombat.db.core :as db]
            [wombat.db.subscriptions :as subscriptions]))

(defn create! [m]
  (let [room (db/create-room! db/spec m)
        subscription (subscriptions/create! {:user-id (:user-id m) :room-id (:id room)})]
    room))
