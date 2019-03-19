(ns wombat.views
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [wombat.events]
            [wombat.subs]
            [wombat.routes :as routes]
            [wombat.util :as util]))

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

(defn textarea [s id placeholder]
  [:textarea {:placeholder placeholder
              :value (get @s id)
              :on-change #(swap! s assoc id (-> % .-target .-value))}])

(defn time-ago
  [m]
  [:time (util/time-ago
          (:created_at m))])

(defn login-form []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div#login-form
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
         [:input.btn {:type "submit"
                      :value "Login"}]]]

       [:div
        [:a {:href (routes/path-for :signup)} "New? Sign Up"]]])))

(defn signup-form []
  (let [s (reagent/atom {:email "" :password ""})]
    (fn []
      [:div#signup-form
       [:h3 "Sign Up"]

       [error-view]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:signup @s]))}
        [:div
         [text-input s :email "Email"]]
        [:div
         [password-input s :password "Password"]]
        [:div
         [:input.btn {:type "submit"
                      :value "Sign Up"}]]]])))

(defn create-room-form []
  (let [s (reagent/atom {:name ""})]
    (fn []
      [:div#create-room-form
       [:h3 "Create Room"]
       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:create-room @s]))}
        [:div
         [text-input s :name "Name your room"]]
        [:div
         [:input.btn {:type "submit"
                      :value "Create"}]]]])))

(defn rooms-list [active]
  (rf/dispatch [:get-rooms])
  (let [rooms (rf/subscribe [:rooms])]
    (fn [active]
      [:aside#rooms-list
       [:ul
        [:li [:a {:href (routes/path-for :new-room)} "+ Create Room"]]
        [:li
         [:a {:class (when (= (:page active) :rooms)
                       "current")
              :href (routes/path-for :rooms)} "Feed"]]
        (for [r @rooms]
          ^{:key (:id r)}
          [:li
           [:a
            {:class (when (and
                           (= (:page active) :room)
                           (= (get-in active [:params :room-id]) (str (:id r))))
                      "current")
             :href (routes/path-for :room :room-id (:id r))}
            (:name r)]])]])))

(defn create-thread-form
  [room-id]
  (let [s (reagent/atom {:subject "" :body ""})]
    (fn []
      [:div.box
       [:div.box-header
        [:span.box-title "Create Thread"]]

       [:form {:on-submit (fn [e]
                            (.preventDefault e)
                            (rf/dispatch [:create-thread room-id @s]))}
        [:div
         [text-input s :subject "Subject"]]
        [:div
         [textarea s :body "Body"]]
        [:div.box-footer
         [:input.btn {:type "submit"
                      :value "Post"}]]]])))

(defn recent-replies [thread-id]
  (let [replies (rf/subscribe [:replies thread-id])]
    (fn [thread-id]
      (rf/dispatch [:get-recent-replies thread-id])
      [:div
       [:div.recent-reply [:a {:href "#"
                               :on-click (fn [e]
                                           (.preventDefault e))} "View more replies"]]
       (for [r @replies]
         ^{:key (:id r)}
         [:div.recent-reply
          [:span.username (:username r)]
          [time-ago r]
          [:p (:body r)]])])))

(defn create-reply-form [thread-id]
  [:div
   [:textarea.reply-textarea {:placeholder "Write a reply..."
                              :on-key-press (fn [e]
                                              (when (and
                                                     (= (.-which e) 13)
                                                     (not (.-shiftKey e)))
                                                (.preventDefault e)
                                                (rf/dispatch [:create-reply thread-id
                                                              {:body (-> e .-target .-value)}])))}]])

(defn thread-box
  [t box-title reply-limit]
  [:div.box.thread
   [:div.box-header
    [:div.box-title box-title]
    [:div.box-subtitle
     [time-ago t]]]
   [:div.box-content
    [:p (:body t)]]
   [:div.box-footer
    [recent-replies (:id t)]
    [create-reply-form (:id t)]]])

(defn threads-list
  [room-id]
  (let [threads (rf/subscribe [:threads])]
    (fn [room-id]
      (rf/dispatch [:get-threads room-id])
      [:div
       (for [t @threads]
         ^{:key (:id t)}
         [thread-box t
          [:span
           [:a
            {:href (routes/path-for :thread :room-id room-id :thread-id (:id t))}
            (:subject t)]
           [:span.username (:username t)]]])])))

(defn room [room-id]
  [:div#room
   [create-thread-form room-id]
   [threads-list room-id]])

(defn replies-list
  [thread-id]
  (rf/dispatch [:get-replies thread-id])
  (let [replies (rf/subscribe [:replies thread-id])]
    (fn [thread-id]
      [:div
       (for [r @replies]
         ^{:key (:id r)}
         [:div
          [:span#username (:username r)]
          [time-ago r]
          [:p (:body r)]])])))

(defn thread
  [thread-id]
  (rf/dispatch [:get-thread thread-id])
  (let [thread (rf/subscribe [:thread])]
    (fn [thread-id]
      [:div#thread
      [:div.thread.box
       [:div.box-header
        [:div.box-title
         [:a {:href "#"} (:subject @thread)]
         [:span.username (:username @thread)]]
        [time-ago @thread]]
       [:div.box-content
        [:p (:body @thread)]]
       [:div.box-footer
        [replies-list thread-id]
        [create-reply-form thread-id]]]])))

(defn feed
  []
  (rf/dispatch [:get-feed])
  (let [threads (rf/subscribe [:feed])]
    (fn []
      [:div#feed
       (for [t @threads]
         ^{:key (:id t)}
         [thread-box t
          [:span
           [:a
            {:href (routes/path-for :room :room-id (:room_id t))}
            (:room_name t)]
           [:span " > "]
           [:a
            {:href (routes/path-for :thread :room-id (:room_id t) :thread-id (:id t))}
            (:subject t)]
           [:span.username (:username t)]]])])))

(defn page
  [{page :page params :params}]
  (case page
    :login [login-form]
    :signup [signup-form]
    :rooms [feed]
    :room [room (:room-id params)]
    :thread [thread (:thread-id params)]
    :new-room [create-room-form]
    [login-form]))

(defn navbar [token]
  [:div#navbar
   [:a#icon {:href "/"}
    [:img#icon {:src "/img/icon.svg"}]]
   [:a#title {:href "/"} "Wombat"]
   (if token
     [:ul
      [:li
       [:a {:href "#" :on-click (fn [e]
                                  (.preventDefault e)
                                  (rf/dispatch [:logout]))} "Logout"]]]
     [:ul
      [:li
       [:a {:href (routes/path-for :login)} "Login"]]
      [:li
       [:a {:href (routes/path-for :signup)} "Sign Up"]]])])

(defn app []
  (let [active (rf/subscribe [:active-page])
        token (rf/subscribe [:token])]
    (fn []
      [:div
       [navbar @token]

       (if @token
         [:div#flex-wrap
          [rooms-list @active]
          [page @active]]
         [page @active])])))
