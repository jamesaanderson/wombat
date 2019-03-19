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
    (-> db
        (assoc :active-page page)
        (assoc :message nil))))

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
  :create-room
  (fn [{db :db} [_ fields]]
    {:http-xhrio {:uri "/api/rooms"
                  :method :post
                  :headers (auth-headers db)
                  :params fields
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:create-room-success]
                  :on-failure []}}))

(rf/reg-event-fx
  :create-room-success
  (fn [{db :db} [_ result]]
    {:db (update-in db [:user :rooms] conj result)
     :set-url [:room :room-id (:id result)]}))

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
  :create-thread
  (fn [{db :db} [_ room-id fields]]
    {:http-xhrio {:uri (str "/api/rooms/" room-id)
                  :method :post
                  :headers (auth-headers db)
                  :params fields
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:create-thread-success]
                  :on-failure [:notify-error]}}))

(rf/reg-event-db
  :create-thread-success
  (fn [db [_ result]]
    (update db :threads conj result)))

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

(rf/reg-event-fx
  :get-feed
  (fn [{db :db} [_]]
    {:http-xhrio {:uri (str "/api/feed")
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-feed-success]
                  :on-failure []}}))

(rf/reg-event-db
  :get-feed-success
  (fn [db [_ result]]
    (assoc db :feed result)))

(rf/reg-event-fx
  :get-recent-replies
  (fn [{db :db} [_ thread-id]]
    {:http-xhrio {:uri (str "/api/threads/" thread-id "/replies?limit=5")
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-replies-success thread-id]
                  :on-failure []}}))

(rf/reg-event-fx
  :get-replies
  (fn [{db :db} [_ thread-id]]
    {:http-xhrio {:uri (str "/api/threads/" thread-id "/replies")
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-replies-success thread-id]
                  :on-failure []}}))

(rf/reg-event-db
  :get-replies-success
  (fn [db [_ thread-id result]]
    (assoc-in db [:replies thread-id] result)))

(rf/reg-event-fx
  :create-reply
  (fn [{db :db} [_ thread-id fields]]
    {:http-xhrio {:uri (str "/api/threads/" thread-id)
                  :method :post
                  :headers (auth-headers db)
                  :params (assoc fields :parent-id nil)
                  :timeout 5000
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:create-reply-success thread-id]
                  :on-failure []}}))

(rf/reg-event-db
  :create-reply-success
  (fn [db [_ thread-id result]]
    (update-in db [:replies thread-id] conj result)))

(rf/reg-event-fx
  :get-thread
  (fn [{db :db} [_ thread-id]]
    {:http-xhrio {:uri (str "/api/threads/" thread-id)
                  :method :get
                  :headers (auth-headers db)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-thread-success]
                  :on-failure []}}))

(rf/reg-event-db
  :get-thread-success
  (fn [db [_ result]]
    (assoc db :thread result)))
