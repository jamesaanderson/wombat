(ns wombat.util
  (:require [goog.date.relative :as rel]))

(defn time-ago
  [s]
  (-> (js/Date. s)
      (.getTime)
      (rel/formatPast)))
