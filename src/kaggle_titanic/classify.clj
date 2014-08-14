(ns kaggle-titanic.classify
  (:require [kaggle-titanic.core :refer [build-tree 
                                         transformers
                                         read-csv-columnar]]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :refer [join split]]))

(defn classify [tree row]
  [(first row)
   (loop [node tree]
     (if (not (contains? node :splitter))
       (if (nil? node)
         (rand-int 2)
         (key (apply max-key val node)))
       (recur (get (:children node) 
                   (nth row (dec (:splitter node)))))))])

(defn transform [l]
  (mapv #(when (> (count %2) 0) (%1 %2)) 
        (subvec transformers 1) 
        l))

(def t (build-tree #{2 4 5 10 11}
                   (read-csv-columnar "resources/train.csv")))

(with-open [i (io/reader "resources/test.csv")
            o (io/writer "submission.csv")]
  (doseq [l (csv/read-csv i)]
    (.write o (str (join "," (classify t (transform l))) "\n"))))

