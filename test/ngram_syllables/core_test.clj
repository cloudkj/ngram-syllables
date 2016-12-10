(ns ngram-syllables.core-test
  (:require [clojure.data.csv :as csv]
            [clojure.test :refer :all]
            [ngram-syllables.core :refer :all]))

(deftest load-corpus
  (testing "Load corpus"
    (let [corpus (with-open [in (clojure.java.io/reader "resources/sample_corpus.csv")]
                   (doall
                    (->> (csv/read-csv in)
                         (map #(drop 2 %)))))]
      (is (not (nil? corpus))))))
