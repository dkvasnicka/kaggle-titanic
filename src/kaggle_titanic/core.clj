(ns kaggle-titanic.core
  (:require [clojure.string :refer [split]]))

(defn columnar-group-by [data col-idx]
  (loop [columns data
         out (into {} (map #(vector % []) (set (nth data col-idx))))]
    (if (empty? (first columns))
      (set (vals out))
      (let [current-row (mapv first columns)
            group-key (nth current-row col-idx)]
        (recur (map rest columns)
               (reduce-kv 
                 (fn [l i r] (update-in l [group-key i] concat [r]))
                 out                  
                 current-row))))))




(defn infogain [data column-idx & {:keys [outcome-idx] :or {outcome-idx 1}}]
  (let [groups (group-by #(nth % column-idx) data)]
    ))

(defn determine-splitter [data candidates]
  (sort-by (partial infogain data) candidates))
