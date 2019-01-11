(ns wombat.events
  (:require [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]))

(defn- auth-headers [token]
  {"Authorization" (str "Token " token)})

(rf/reg-event-db
  :set-active-panel
  (fn [db [_ panel]]
    (assoc db :active-panel panel)))

(rf/reg-event-db
  :notify-error
  (fn [db [_ result]]
    (assoc db :error-message
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

(rf/reg-event-db
  :auth-success
  (fn [db [_ result]]
    (let [token (:token result)]
      (rf/dispatch [:get-rooms token])
      (rf/dispatch [:set-active-panel :rooms])

      (-> db
          (dissoc :error-message)
          (assoc :token token)))))

(rf/reg-event-fx
  :get-rooms
  (fn [_ [_ token]]
    {:http-xhrio {:uri "/api/rooms"
                  :method :get
                  :headers (auth-headers token)
                  :timeout 8000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [:get-rooms-success]
                  :on-failure []}}))

(rf/reg-event-db
  :get-rooms-success
  (fn [db [_ result]]
    (assoc-in db [:user :rooms] result)))
