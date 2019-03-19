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

(rf/reg-sub
  :replies
  (fn [db [_ thread-id]]
    (get-in db [:replies thread-id])))

(rf/reg-sub
  :thread
  (fn [db]
    (:thread db)))

(rf/reg-sub
  :feed
  (fn [db]
    (:feed db)))
