(ns wombat.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response bad-request resource-response content-type]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [wombat.db.users :as users]
            [wombat.db.rooms :as rooms]
            [wombat.db.subscriptions :as subscriptions]
            [wombat.db.threads :as threads]
            [buddy.sign.jwt :as jwt]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [environ.core :refer [env]]
            [clj-time.core :as t]))

(def secret (env "JWT_SECRET"))
(def auth-backend (backends/jws {:secret secret}))

(def unauthorized-response
  (-> (response {:message "Unauthorized request."})
      (assoc :status 401)))

(defn- claims [user]
  (-> user
      (select-keys [:id :email :username])
      (assoc :iat (t/now))
      (assoc :exp
             (t/plus (t/now) (t/days 1)))))

(defn- jwt-response [user]
  (response
    {:token (jwt/sign (claims user) secret)}))

(defn signup
  [request]
  (let [email (get-in request [:body :email])
        password (get-in request [:body :password])
        user (users/create! email password)]
    (if (:success? user)
      (jwt-response user)
      (bad-request {:message (:message user)}))))

(defn login
  [request]
  (let [email (get-in request [:body :email])
        password (get-in request [:body :password])
        user (users/validate email password)]
    (if (:valid? user)
      (jwt-response user)
      (bad-request {:message "Invalid credentials."}))))

(defn create-room
  [request]
  (if (authenticated? request)
    (if-let [r-name (get-in request [:body :name])]
      (response 
        (rooms/create! {:name r-name
                        :user-id (get-in request [:identity :id])}))
      (bad-request {:message "Empty room name."}))
    unauthorized-response))

(defn subscribe
  [request]
  (if (authenticated? request)
    (let [room-id (-> request
                      (get-in [:route-params :id])
                      (Integer/parseInt))]

      ;; TODO: validate input, catch exception

      (response
        (subscriptions/create! {:user-id (get-in request [:identity :id]) :room-id room-id})))
    unauthorized-response))

(defn rooms
  [request]
  (if (authenticated? request)
    (response
      (subscriptions/get-by-user-id (get-in request [:identity :id])))
    unauthorized-response))

(defn create-thread
  [request]
  (if (authenticated? request)
    (let [room-id (-> request
                      (get-in [:route-params :id])
                      (Integer/parseInt))
          subject (get-in request [:body :subject])
          body (get-in request [:body :body])]

      ;; TODO: validate input, catch exception

      (response
        (threads/create! {:subject subject
                          :body body
                          :user-id (get-in request [:identity :id])
                          :room-id room-id})))
    unauthorized-response))

(defn threads
  [request]
  "")

(defroutes api-routes
  (context "/api" []
           ;; AUTH
           (POST "/signup" [] signup)
           (POST "/login" [] login)

           ;; ROOMS
           (GET "/rooms" [] rooms)
           (POST "/rooms" [] create-room)
           (POST "/rooms/:id/subscribe" [] subscribe)

           ;; THREADS
           (GET "/rooms/:id" [] threads)
           (POST "/rooms/:id" [] create-thread)))

(defroutes app-routes
  (GET "/" [] (-> (resource-response "index.html" {:root "public"})
                  (content-type "text/html; charset=utf-8")))
  (route/resources "/"))

(def app
  (routes (-> api-routes
              (wrap-routes
                #(wrap-authentication % auth-backend))
              (wrap-routes
                #(wrap-json-body % {:keywords? true}))
              (wrap-routes wrap-json-response))
          app-routes))

(defn -main
  [port & args]
  (jetty/run-jetty app {:port (Integer/parseInt port)}))

(defn -dev-main
  [& args]
  (jetty/run-jetty (wrap-reload #'app) {:port 1337}))
