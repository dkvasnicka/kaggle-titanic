(ns kaggle-titanic.classify
  (:require [kaggle-titanic.core :refer [build-tree 
                                         apply-rules
                                         read-csv-columnar
                                         htmlize-tree]]
            [kaggle-titanic.classify :refer :all]
            [clojure.java.io :as io]
            [hiccup.core :refer :all]
            [clojure.data.csv :as csv]
            [clojure.string :refer [join]]))

; Transformer functions for columns in the train/test files
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
   identity
   first
   identity])

; Build the tree
(def t (build-tree #{2 4 5 11}
                   (read-csv-columnar "resources/train.csv" transformers)))

; Output it in the form of HTML
(spit "tree.htm" (html (htmlize-tree t)))

; Stream the test data set and classify the rows
(with-open [i (io/reader "resources/test.csv")
            o (io/writer "submission.csv")]
  (.write o "PassengerId,Survived\n")
  (doseq [l (csv/read-csv i)]
    (.write o (str (join "," (classify t (transform-line l transformers))) "\n"))))
