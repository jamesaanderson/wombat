(ns wombat.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [wombat.events]
            [wombat.subs]
            [wombat.views :as views]))

(defn- wrap-auth
  [f]
  (let [token (rf/subscribe [:token])]
    (fn []
      (if @token
        [f token]
        [rf/dispatch [:set-active-panel :login]]))))

(defn- page
  [panel]
  (case panel
    :login [views/login-form]
    :signup [views/signup-form]
    :rooms [wrap-auth views/rooms-list]
    [views/login-form]))

(defn app
  []
  (let [active (rf/subscribe [:active-panel])]
    (fn []
      [page @active])))

(defn ^:export main []
  (reagent/render [app]
                  (js/document.getElementById "app")))
