(ns ngram-syllables.core)

;; N-gram

(def start-sentinel :start)
(def end-sentinel :end)

(defn ngram-counts
  [n corpus]
  (->> corpus
       (map #(cons start-sentinel (conj (vec %) end-sentinel)))
       (map #(partition n 1 %))
       (apply concat)
       (reduce (fn [m p] (assoc-in m p (inc (get-in m p 0)))) {})))

(defn make-ngram-model
  [n corpus]
  (reduce (fn [m i] (assoc m i (ngram-counts i corpus))) {} (range 1 (inc n))))

(defn get-counts
  [model n]
  (get model n))

(defn ngram-probability
  "Returns the probability of token t appearing after a seq of tokens s."
  ([model t]
   (ngram-probability model [] t))
  ([model s t]
   (let [n (inc (count s))
         numer (get-in (get-counts model n) (conj (vec s) t) 0)
         denom (if (empty? s)
                 (get (get-counts model 1) start-sentinel 0)
                 (get-in (get-counts model (- n 1)) s 0))]
     (if (= denom 0) 0.0 (float (/ numer denom))))))

;; Syllables

(defn candidates
  ([model word]
   (candidates model word []))
  ([model word syllables]
   ;; Filter out syllabifications that contain unknown syllables
   (let [known? (fn [x] (contains? (get-counts model 1) x))]
     (cond (empty? word)      (filter #(or (= (count %) 1) (known? (last %))) syllables)
           (empty? syllables) (recur model (subs word 1) (list (vector (subs word 0 1))))
           :else              (recur model (subs word 1)
                                     (let [c (subs word 0 1)
                                           add    #(conj % c)
                                           append #(assoc % (dec (count %)) (str (last %) c))]
                                       (concat
                                        (map add (filter #(known? (last %)) syllables))
                                        (map append syllables))))))))

(defn unigram-predict
  [model word]
  (->> (candidates model word)
       (apply max-key (fn [x] (reduce * (map #(ngram-probability model %) x))))))

(defn bigram-predict
  [model word]
  (->> (candidates model word)
       (apply max-key (fn [x]
                        (->> (cons start-sentinel (conj x end-sentinel))
                             (partition 2 1)
                             (map #(ngram-probability model (butlast %) (last %)))
                             (reduce *))))))

;; TODO: add generic prediction function
