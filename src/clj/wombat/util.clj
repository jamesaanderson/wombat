(ns wombat.util)

(defn- expand-validation-error
  [{ke :keys msg :msg}]
  (reduce #(assoc %1 %2 [msg]) {} ke))

(defn format-validation-errors
  [errors]
  (->> errors
       (map expand-validation-error)
       (reduce #(merge-with into %1 %2) {})))
