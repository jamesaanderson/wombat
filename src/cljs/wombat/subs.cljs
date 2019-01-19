(ns wombat.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :active-page
  (fn [db]
    (:active-page db)))

(rf/reg-sub
  :message
  (fn [db]
    (:message db)))

(rf/reg-sub
  :token
  (fn [db]
    (:token db)))

(rf/reg-sub
  :rooms
  (fn [db]
    (get-in db [:user :rooms])))

(rf/reg-sub
  :threads
  (fn [db]
    (:threads db)))
