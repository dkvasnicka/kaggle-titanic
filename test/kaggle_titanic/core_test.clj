(ns kaggle-titanic.core-test
  (:require [kaggle-titanic.core :refer :all]
            [midje.sweet :refer :all]))

(fact "Columnar group by works"
      (columnar-group-by ['(1 2 3 4 5) '(1 1 2 2 1) '(:a :b :c :d :e)] 1) 
      => '(((4 3)   (2 2)   (:d :c))
           ((5 2 1) (1 1 1) (:e :b :a))))
