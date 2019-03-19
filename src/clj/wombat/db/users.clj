(ns wombat.db.users
  (:require [wombat.db.core :as db]
            [wombat.db.names :as names]
            [wombat.db.adjectives :as adjectives]
            [buddy.hashers :as hashers]
            [jkkramer.verily :as v]))

(def validations [[:required [:email :password]]
                  [:min-length 6 :password]
                  [:email :email]])

(defn- ceil [n] (int (Math/ceil n)))

(defn- generate-username
  "Hashing function of sorts. Given a unique user id, generate a unique, user-friendly username"
  [id]
  (let [n-names (names/count-names)
        n-adjectives (adjectives/count-adjectives)
        name-id (+ (mod (- id 1) n-names) 1)
        adjective-id (ceil (/ id n-names))
        adj-name-str (str (adjectives/get-adjective adjective-id) "-" (names/get-name name-id))]
    (if (> adjective-id n-adjectives)
      (str adj-name-str "-" id)
      adj-name-str)))

(defn update-username! [m]
  (db/update-user-username! db/spec m))

(defn get-by-email [email]
  (db/get-user-by-email db/spec {:email email}))

(defn create!
  [email password]
  (let [encrypted (hashers/derive password)
        errors (v/validate {:email email :password password} validations)
        valid? (or (empty? errors) (nil? errors))]
    (if valid?
      (if (get-by-email email)
        {:success? false :message "A user already exists with the given email."}
        (try
          (let [user (db/create-user! db/spec {:email email :password encrypted})
                id (:id user)]
            (assoc (update-username! {:id id :username (generate-username id)}) :success? true))
          (catch Exception _
            {:success? false :message "Error creating user."})))
      {:success? false :message errors})))

(defn validate
  [email password]
  (let [user (get-by-email email)
        valid? (hashers/check password (:password user))]
    (assoc user :valid? valid?)))
