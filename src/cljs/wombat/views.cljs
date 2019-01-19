(ns wombat.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [wombat.events]
            [wombat.subs]
            [wombat.routes :as routes]))

(defn error-view []
  (let [message (rf/subscribe [:message])]
    (fn []
      (when @message
        [:p.error @message]))))

(defn input [input-type s id placeholder]
  [:input {:type input-type
           :placeholder placeholder
           :value (get @s id)
           :on-change #(swap! s assoc id (-> % .-target .-value))}])

(defn text-input [s id placeholder]
  [input "text" s id placeholder])

(defn password-input [s id placeholder]
  [input "password" s id placeholder])

(defn login-form []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div
       [:h3 "Login"]

       [error-view]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:login @s]))}
        [:div
         [text-input s :email "Email"]]
        [:div
         [password-input s :password "Password"]]
        [:div
         [:input {:type "submit"
                  :value "Login"}]]]

       [:div
        [:a {:href (routes/path-for :signup)} "New? Sign Up"]]])))

(defn signup-form []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div
       [:h3 "Sign Up"]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:signup @s]))}
        [:div
         [text-input s :email "Email"]]
        [:div
         [password-input s :password "Password"]]
        [:div
         [:input {:type "submit"
                  :value "Sign Up"}]]]])))

(defn rooms-list []
  (rf/dispatch [:get-rooms])
  (let [rooms (rf/subscribe [:rooms])]
    (fn []
      [:div
       (for [r @rooms]
         [:a
          {:key (:room_id r)
           :href (routes/path-for :threads :room-id (:room_id r))}
          (:name r)])])))

(defn threads-list
  [room-id]
  (rf/dispatch [:get-threads room-id])
  (let [threads (rf/subscribe [:threads])]
    (fn [room-id]
      [:div
       (for [t @threads]
         [:div {:key (:id t)}
          [:h2 (:subject t)]
          [:p (:body t)]])])))

(defn page
  [{page :page params :params}]
  (let [token (rf/subscribe [:token])]
    (fn [{page :page params :params}]
      (case page
        :login [login-form]
        :signup [signup-form]
        :rooms (if @token [rooms-list] [login-form])
        :threads (if @token [threads-list (:room-id params)] [login-form])
        [login-form]))))

(defn navbar []
  (let [token (rf/subscribe [:token])]
    (fn []
      [:div#navbar
       [:a#icon {:href "/"}
        [:img#icon {:src "/img/icon.svg"}]]
       [:a#title {:href "/"} "Wombat"]
       (if @token
         [:ul
          [:li
           [:a {:href "#" :on-click (fn [e]
                                      (.preventDefault e)
                                      (rf/dispatch [:logout]))} "Logout"]]]
         [:ul
          [:li
           [:a {:href (routes/path-for :login)} "Login"]]
          [:li
           [:a {:href (routes/path-for :signup)} "Sign Up"]]])])))

(defn app []
  (let [active (rf/subscribe [:active-page])]
    (fn []
      [:div
       [navbar]
       [:div#content
         [page @active]]])))
