(ns wombat.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [wombat.events]
            [wombat.views :as views]
            [wombat.routes :as routes]))

(defn mount-components []
  (reagent/render [views/app] (js/document.getElementById "app")))

(defn ^:export main []
  (routes/start!)
  (rf/dispatch-sync [:initialize-db])
  (mount-components))
