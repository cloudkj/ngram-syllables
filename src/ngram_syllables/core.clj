(ns ngram-syllables.core
  (:require [clojure.string :refer [join]]))

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

(defn ngram-model-stats
  [model]
  (->> (sort (keys model))
       (map #(str (count (get model %)) " " % "-gram sequences"))
       (join \newline)))

(defn get-counts
  [model n]
  (get model n))

(defn ngram-probability
  "Returns the probability of token t appearing after a seq of tokens s."
  ([model t]
   (ngram-probability model [] t))
  ([model s t]
   (let [n (inc (count s))
         ;; TODO: add smoothing
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

(defn ngram-predict-candidate
  [n model candidate]
  (if (= n 1)
    ;; TODO: use log space for probabilities
    (reduce * (map #(ngram-probability model %) candidate))
    (->> (cons start-sentinel (conj candidate end-sentinel))
         (partition n 1)
         (map #(ngram-probability model (butlast %) (last %)))
         ;; TODO: use log space for probabilities
         (#(if (empty? %) 0 (reduce * %))))))

(defn ngram-predict
  "Returns a prediction using n-grams."
  [n model word]
  (apply max-key #(ngram-predict-candidate n model %) (candidates model word)))

(defn predict
  "Returns a prediction based on a weighted interpolation of predictions using
  1-grams, 2-grams, ..., and n-grams."
  [weights model word]
  (let [n (count weights)]
    (->> (candidates model word)
         (apply max-key (fn [c]
                          (->> (map #(* (ngram-predict-candidate %1 model c) %2)
                                    (range 1 (inc n))
                                    weights)
                               (reduce +)))))))
