(ns kaggle-titanic.core
  (:require [clojure.string :refer [split]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

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
     :candidate column-idx
     :groups groups}))

(defn split-data [data candidates]
  (apply 
    max-key 
    :infogain
    (map (partial groups-and-infogain data) candidates)))

(defn apply-rules [rules value]
  (some #(when (<= (first %) 
                   (or value (dec (first %))) 
                   (second %)) 
           (nth % 2)) 
        rules))

(def transformers
  [identity
   identity
   identity
   identity
   identity
   (comp
     (partial apply-rules [[0 10 :kid]
                           [11 18 :adolescent]
                           [19 30 :young]
                           [31 55 :adult]
                           [56 100 :elderly]])
     read-string
     #(clojure.string/replace % #"," "."))
   identity
   identity
   identity
   first
   identity])

(defn read-csv-columnar [path]
  (with-open [in-file (io/reader path)]
    (let [[header & data] (csv/read-csv in-file)]
      (reduce (fn [colz row]
                (mapv #(cons (when (> (count %2) 0) (%1 %2)) %3) 
                      transformers row colz)) 
              (repeat (count header) '()) 
              data))))

(defn build-tree [candidates data]
  (if (or (empty? candidates) (= 1 (ccount data)))
    (frequencies (nth data 1))
    (let [splitted (split-data data candidates)
          splitter (:candidate splitted)
          remaining-candidates (disj candidates splitter)]
      {splitter
       (map (partial build-tree remaining-candidates)
            (:groups splitted))})))
