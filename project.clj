(defproject wombat "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.439"]
                 [ring "1.7.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
                 [org.postgresql/postgresql "42.2.2"]
                 [com.layerware/hugsql "0.4.9"]
                 [migratus "1.2.0"]
                 [buddy "2.0.0"]
                 [jkkramer/verily "0.6.0"]
                 [environ "1.1.0"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [cljs-ajax "0.8.0"]]
  :plugins [[migratus-lein "0.7.0"]
            [lein-environ "1.1.0"]
            [lein-figwheel "0.5.18"]]
  :repl-options {:init-ns wombat.core}
  :main wombat.core
  :profiles {:project/dev {:main wombat.core/-dev-main}
             :profiles/dev {}
             :dev [:project/dev :profiles/dev]}
  :migratus {:store :database
             :migration-dir "migrations"
             :db {:classname "org.postgresql.Driver"
                  :subprotocol "postgresql"
                  :subname ~(get (System/getenv) "DB_SUBNAME")
                  :user ~(get (System/getenv) "DB_USER")
                  :password ~(get (System/getenv) "DB_PASS")}}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel true
                        :compiler {:main "wombat.core"
                                   :asset-path "js/compiled/out"
                                   :output-dir "resources/public/js/compiled/out"
                                   :output-to "resources/public/js/compiled/app.js"}}]}
  :source-paths ["src/clj" "src/cljs"])
