(ns wombat.migrations.import-words
  (:require [clojure.java.io :as io]
            [wombat.db.adjectives :as adjectives]
            [wombat.db.names :as names]))

(defn import-words
  "Import words provided a file and a db function for creation."
  [file f]
  (with-open [rdr (io/reader (io/resource file))]
    (doseq [l (-> rdr
                  (line-seq)
                  (shuffle))]
      (f l))))

(defn migrate-up
  [config]
  (import-words "words/adjectives.txt" adjectives/create!)
  (import-words "words/names.txt" names/create!))
