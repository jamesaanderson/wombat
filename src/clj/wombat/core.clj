(ns wombat.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response bad-request resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.coercions :refer [as-int]]
            [compojure.route :as route]
            [wombat.db.users :as users]
            [wombat.db.rooms :as rooms]
            [wombat.db.subscriptions :as subscriptions]
            [wombat.db.threads :as threads]
            [wombat.db.replies :as replies]
            [buddy.sign.jwt :as jwt]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [environ.core :refer [env]]
            [clj-time.core :as t]))

(def secret (env "JWT_SECRET"))
(def auth-backend (backends/jws {:secret secret}))

(defn- claims [user]
  (-> user
      (select-keys [:id :email :username])
      (assoc :iat (t/now))
      (assoc :exp
             (t/plus (t/now) (t/days 1)))))

(defn- jwt-response [user]
  (-> user
      (select-keys [:id :email :username :created_at])
      (assoc :token (jwt/sign (claims user) secret))
      (response)))

(defn signup
  [{{:keys [email password]} :body}]
  (let [user (users/create! email password)]
    (if (:success? user)
      (jwt-response user)
      (bad-request {:message (:message user)}))))

(defn login
  [{{:keys [email password]} :body}]
  (let [user (users/validate email password)]
    (if (:valid? user)
      (jwt-response user)
      (bad-request {:message "Invalid email or password."}))))

(defn get-rooms
  [request]
  (response
   (subscriptions/get-by-user-id (get-in request [:identity :id]))))

(defn create-room
  [{body :body
    {user-id :id} :identity}]
  (let [room (rooms/create! (-> body
                                (assoc :user-id user-id)))]
    (if (:success? room)
      (response (dissoc room :success?))
      (bad-request {:message (:message room)}))))

(defn subscribe
  [m]
  (let [subscription (subscriptions/create! m)]
    (if (:success? subscription)
      (response (dissoc subscription :success?))
      (bad-request {:message (:message subscription)}))))

(defn get-feed
  [{{user-id :id} :identity}]
  (response (threads/get-feed user-id)))

(defn get-threads
  [room-id]
  (response (threads/get-by-room-id room-id)))

(defn create-thread
  [m]
  (let [thread (threads/create! m)]
    (if (:success? thread)
      (response (dissoc thread :success?))
      (bad-request {:message (:message thread)}))))

(defn get-thread
  [id]
  (response
    (threads/get-by-id id)))

(defn get-replies
  [thread-id limit]
  (if limit
    (response
     (replies/get-by-thread-id thread-id limit))
    (response
     (replies/get-by-thread-id thread-id))))

(defn create-reply
  [m]
  (let [reply (replies/create! m)]
    (if (:success? reply)
      (response (dissoc reply :success?))
      (bad-request {:message (:message reply)}))))

(defroutes api-public-routes
  (context "/api" []
    ;; AUTH
    (POST "/signup" [] signup)
    (POST "/login" [] login)))

(defroutes api-protected-routes
  (context "/api" []
    ;; ROOMS
    (GET "/rooms" [] get-rooms)
    (POST "/rooms" [] create-room)
    (POST "/rooms/:id/subscribe" [id :<< as-int
                                  :as {{user-id :id} :identity}]
      (subscribe {:room-id id :user-id user-id}))

    ;; THREADS
    (GET "/feed" [] get-feed)
    (GET "/rooms/:id" [id :<< as-int] (get-threads id))
    (POST "/rooms/:id" [id :<< as-int
                        :as {body :body
                             {user-id :id} :identity}]
      (create-thread (-> body
                         (assoc :user-id user-id)
                         (assoc :room-id id))))
    (GET "/threads/:id" [id :<< as-int] (get-thread id))
           
    ;; REPLIES
    (GET "/threads/:id/replies" [id :<< as-int
                                 limit]
      (try
        (get-replies id (Long/parseLong limit))
        (catch Exception _
          (get-replies id nil))))
    (POST "/threads/:id" [id :<< as-int
                          :as {body :body
                               {user-id :id} :identity}]
      (create-reply (-> body
                        (assoc :user-id user-id)
                        (assoc :thread-id id))))))

(defroutes app-routes
  (route/resources "/")
  ;; send all routes to SPA
  (route/not-found (-> (resource-response "index.html" {:root "public"})
                       (content-type "text/html; charset=utf-8"))))

(defn- wrap-authorization
  [handler]
  (fn [request]
    (if (authenticated? request)
      (handler request)
      (-> (response {:message "Unauthorized request."})
          (assoc :status 401)))))

(def app
  (routes (-> api-public-routes
              (wrap-routes
               #(wrap-json-body % {:keywords? true}))
              (wrap-routes wrap-json-response))
          (-> api-protected-routes
              (wrap-routes wrap-authorization)
              (wrap-routes
               #(wrap-authentication % auth-backend))
              (wrap-routes
               #(wrap-json-body % {:keywords? true}))
              (wrap-routes wrap-json-response)
              (wrap-routes wrap-params))
          app-routes))

(defn -main [port & args]
  (jetty/run-jetty app {:port (Integer/parseInt port)}))

(defn -dev-main [& args]
  (jetty/run-jetty (wrap-reload #'app) {:port 1337}))
