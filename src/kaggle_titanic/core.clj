(ns kaggle-titanic.core
  (:require [clojure.string :refer [split]]))

(def sum (partial reduce +))

(defn columnar-group-by [data col-idx]
  (loop [columns data
         out {}]
    (if (empty? (first columns))
      (map vals (vals out))
      (let [current-row (mapv first columns)
            group-key (nth current-row col-idx)]
        (recur (map rest columns)
               (reduce-kv 
                 (fn [l i r] (update-in l [group-key i] conj r))
                 out                  
                 current-row))))))

(defn log2 [n] (/ (Math/log n) (Math/log 2)))

(defn entropy [col]
  (let [freqs (frequencies col)
        total (count col)]
    (sum (map #(* -1 (let [r (/ % total)] (* r (log2 r)))) 
               (vals freqs)))))

(def ccount (comp count first))

(defn dataset-entropy [outcome-idx data]
  (entropy (nth data outcome-idx)))

(defn groups-and-infogain 
  [data column-idx & {:keys [outcome-idx] :or {outcome-idx 1}}]
  
  (let [groups (columnar-group-by data column-idx)
        total-size (ccount data)
        group-sizes (map ccount groups)
        entropy-current (dataset-entropy outcome-idx data)
        entropies-split (map (partial dataset-entropy outcome-idx) groups)
        entropy-after (sum (map #(* %2 (/ %1 total-size)) 
                                 group-sizes entropies-split))]
    {:infogain (- entropy-current entropy-after)
     :groups groups}))

(defn split-data [data candidates]
  (max-key 
    :infogain
    (map (partial groups-and-infogain data) candidates)))
