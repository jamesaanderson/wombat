(ns wombat.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :active-panel
  (fn [db _]
    (:active-panel db)))

(rf/reg-sub
  :error-message
  (fn [db _]
    (:error-message db)))

(rf/reg-sub
  :token
  (fn [db _]
    (:token db)))

(rf/reg-sub
  :rooms
  (fn [db _]
    (get-in db [:user :rooms])))
