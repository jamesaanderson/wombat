(ns wombat.db.threads
  (:require [wombat.db.core :as db]
            [jkkramer.verily :as v]))

(def validations [[:required [:body]]])

(defn create! [thread]
  (let [errors (v/validate thread validations)
        valid? (or (empty? errors) (nil? errors))]
    (if valid?
      (try
        (-> (db/create-thread! db/spec thread)
            (assoc :success? true))
        (catch Exception _
          {:success? false :message "Error creating thread."}))
      {:success? false :message errors})))

(defn get-by-room-id [room-id]
  (db/get-threads-by-room-id db/spec {:room-id room-id}))

(defn get-by-id [id]
  (db/get-threads-by-id db/spec {:id id}))

(defn get-feed [user-id]
  (db/get-feed db/spec {:user-id user-id}))
