(ns wombat.db.core
  (:require [jdbc.pool.c3p0 :as pool]
            [hugsql.core :as hugsql]
            [hugsql.adapter.clojure-java-jdbc :as adp]
            [environ.core :refer [env]]))

(def spec
  (pool/make-datasource-spec
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (env :db-subname)
     :user (env :db-user)
     :password (env :db-pass)}))

(hugsql/set-adapter! (adp/hugsql-adapter-clojure-java-jdbc))

(hugsql/def-db-fns "wombat/db/sql/adjectives.sql")
(hugsql/def-db-fns "wombat/db/sql/names.sql")
(hugsql/def-db-fns "wombat/db/sql/rooms.sql")
(hugsql/def-db-fns "wombat/db/sql/subscriptions.sql")
(hugsql/def-db-fns "wombat/db/sql/threads.sql")
(hugsql/def-db-fns "wombat/db/sql/users.sql")
(hugsql/def-db-fns "wombat/db/sql/replies.sql")
