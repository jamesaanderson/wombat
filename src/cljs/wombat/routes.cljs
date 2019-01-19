(ns wombat.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

(def routes
  ["/" {"login" :login
        "signup" :signup
        "rooms" {"" :rooms
                 ["/" :room-id] :threads}}])

(def parse-url (partial bidi/match-route routes))

(defn- dispatch-route [{handler :handler route-params :route-params}]
  (rf/dispatch [:set-active-page {:page handler :params route-params}]))

(def history
  (pushy/pushy dispatch-route parse-url))

(defn start! []
  (pushy/start! history))

(def path-for (partial bidi/path-for routes))
(def set-token! (partial pushy/set-token! history))
