(ns wombat.events
  (:require [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [wombat.routes :as routes]))

(defn- auth-headers [db]
  {"Authorization" (str "Token " (:token db))})

;; EFFECTS

(rf/reg-fx
  :set-url
  (fn [args]
    (routes/set-token!
      (apply routes/path-for args))))

(rf/reg-fx
  :set-token-local-storage
  (fn [token]
    (.setItem js/localStorage "token" token)))

(rf/reg-fx
  :remove-token-local-storage
  (fn [_]
    (.removeItem js/localStorage "token")))

(rf/reg-cofx
  :get-token-local-storage
  (fn [cofx _]
    (assoc cofx :token
           (.getItem js/localStorage "token"))))

;; EVENTS

(rf/reg-event-fx
  :initialize-db
  [(rf/inject-cofx :get-token-local-storage)]
  (fn [cofx _]
    {:db cofx}))

(rf/reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(rf/reg-event-db
  :notify-error
  (fn [db [_ result]]
    (assoc db :message
           (get-in result [:response :message]))))

(rf/reg-event-fx
  :login
  (fn [_ [_ fields]]
    {:http-xhrio {:uri "/api/login"
                  :method :post
                  :params fields
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:auth-success]
                  :on-failure [:notify-error]}}))

(rf/reg-event-fx
  :signup
  (fn [_ [_ fields]]
    {:http-xhrio {:uri "/api/signup"
                  :method :post
                  :params fields
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:auth-success]
                  :on-failure [:notify-error]}}))

(rf/reg-event-fx
  :auth-success
  (fn [{db :db} [_ {token :token}]]
    {:db (assoc db :token token)
     :set-token-local-storage token
     :set-url [:rooms]}))

(rf/reg-event-fx
  :logout
  (fn [{db :db} _]
    {:db (dissoc db :token)
     :remove-token-local-storage nil
     :set-url [:login]}))

(rf/reg-event-fx
  :get-rooms
  (fn [{db :db} _]
    {:http-xhrio {:uri "/api/rooms"
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-rooms-success]
                  :on-failure []}}))

(rf/reg-event-db
  :get-rooms-success
  (fn [db [_ result]]
    (assoc-in db [:user :rooms] result)))

(rf/reg-event-fx
  :get-threads
  (fn [{db :db} [_ room-id]]
    {:http-xhrio {:uri (str "/api/rooms/" room-id)
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-threads-success]
                  :on-failure []}}))

(rf/reg-event-db
  :get-threads-success
  (fn [db [_ result]]
    (assoc db :threads result)))
