(ns wombat.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))

(defn login-form
  []
  [:div
   [:h3 "login"]
   [:form
    [:div
      [:input {:type "text"
               :placeholder "email"}]]
    [:div
      [:input {:type "password"
               :placeholder "password"}]]
    [:div
      [:input {:type "submit"
               :value "login"}]]]])

(defn ^:export main []
  (reagent/render [login-form]
                  (js/document.getElementById "app")))
