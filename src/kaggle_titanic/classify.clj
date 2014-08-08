(ns kaggle-titanic.classify
  (:require [kaggle-titanic.core :refer [build-tree 
                                         transformers
                                         read-csv-columnar]]
            [clojure.java.io :as io]
            [clojure.string :refer [join]]))

(defn classify [tree row]
  [1 1])

(def t (build-tree #{2 4 5 10 11}
                   (read-csv-columnar "resources/train.csv")))

(with-open [i (io/reader "resources/test.csv")
            o (io/writer "submission.csv")]
  (doseq [l (line-seq i)]
    (.write o (str (join "," (classify t l)) "\n"))))

