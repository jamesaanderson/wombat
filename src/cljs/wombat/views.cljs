(ns wombat.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [wombat.events]
            [wombat.subs]))

(defn error-view
  []
  (let [error-message (rf/subscribe [:error-message])]
    (fn []
      (when @error-message
        [:p @error-message]))))

(defn login-form
  []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div
       [:h3 "login"]

       [error-view]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:login @s]))}
        [:div
          [:input {:type "text"
                   :placeholder "email"
                   :value (:email @s)
                   :on-change #(swap! s assoc :email (-> % .-target .-value))}]]
        [:div
         [:input {:type "password"
                  :placeholder "password"
                  :value (:password @s)
                  :on-change #(swap! s assoc :password (-> % .-target .-value))}]]
        [:div
         [:input {:type "submit"
                  :value "login"}]]]

       [:div
        [:a {:href "#"
             :on-click (fn [e]
                         (.preventDefault e)
                         (rf/dispatch [:set-active-panel :signup]))}
          "sign up"]]])))

(defn signup-form
  []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div
       [:h3 "sign up"]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:signup @s]))}
        [:div
         [:input {:type "text"
                  :placeholder "email"
                  :value (:email @s)
                  :on-change #(swap! s assoc :email (-> % .-target .-value))}]]
        [:div
         [:input {:type "password"
                  :placeholder "password"
                  :value (:password @s)
                  :on-change #(swap! s assoc :password (-> % .-target .-value))}]]
        [:div
         [:input {:type "submit"
                  :value "sign up"}]]]])))

(defn rooms-list
  [token]
  (let [rooms (rf/subscribe [:rooms])]
    (fn [token]
      (for [r @rooms]
        [:p (:name r)]))))
