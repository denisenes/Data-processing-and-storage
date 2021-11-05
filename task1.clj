;; ["a" "b" "c"] "a" ----> ["aa" "ab" "ac"]  --filter--> ["ab" "ac"]
(defn cc
  [lst a]
  (filter (fn [x] (let [[f s] x] (not= f s)))
          (map (fn [x] (str a x)) lst)))

;; make all combinations of characters from two lists
;; ["a" "b"] ["a" "b"] --> ["aa" "ab" "ba" "bb"]
(defn make_comb
  [l1 l2]
  (reduce (fn [acc x] (concat acc (cc l1 x))) () l2))

;; =========================================================
;; clean alphabet from duplicates
(defn search
  [x l]
  (reduce (fn [acc y] (if (= x y) true 
                          (if (= acc false) false true))) false l))

(defn clean
  [alph]
  (reduce (fn [acc x] (if (= (search x acc) true) acc (cons x acc))) () alph))
;;==========================================================

;; main func
;; apply make_comb N-1 times to the alphabet
(defn foo
  [alph N]
  (if (= N 0) ()
      (let [clean_alph (clean alph)] 
        (reduce (fn [acc _] (make_comb acc clean_alph)) clean_alph (range (dec N))))))

(let [abs (list "c" "b" "b" "b" "b" "a")
      N 4]
  (println (foo abs N)))
