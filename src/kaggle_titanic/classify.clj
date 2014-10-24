(ns kaggle-titanic.classify)

(defn classify [tree row]
  [(first row)
   (loop [node tree]
     (if (not (contains? node :splitter))
       (if (nil? node)
         (rand-int 2)
         (key (apply max-key val node)))
       (recur (get (:children node) 
                   (nth row (dec (:splitter node)))))))])

(defn transform-line [l transformers]
  (mapv #(when (> (count %2) 0) (%1 %2)) 
        (subvec transformers 1) 
        l))
