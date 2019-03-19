(ns wombat.db.replies
  (:require [wombat.db.core :as db]
            [jkkramer.verily :as v]))

(def validations [[:required [:body]]])

(defn create! [reply]
  (let [errors (v/validate reply validations)
        valid? (or (empty? errors) (nil? errors))]
    (if valid?
      (try
        (-> (db/create-reply! db/spec reply)
            (assoc :success? true))
        (catch Exception _
          {:success? false :message "Error creating reply."}))
      {:success? false :message errors})))

(defn get-by-thread-id
  ([thread-id]
   (db/get-replies-by-thread-id db/spec {:thread-id thread-id}))

  ([thread-id limit]
   (db/get-replies-by-thread-id-limit db/spec {:thread-id thread-id
                                               :limit limit})))
