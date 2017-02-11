(ns ngram-syllables.train
  (:require [clojure.data.csv :as csv]
            [clojure.string :refer [join]]
            [clojure.tools.cli :as cli]
            [ngram-syllables.core :refer :all]
            [ngram-syllables.util :refer :all]))

(def usage
  (str "Usage: lein run -m " (ns-name *ns*) " [options] corpus"))

(def cli-opts
  [["-h" "--help"]
   ["-n" "--n GRAMS" "Number of grams"
    :default 1
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "n must be greater than 0"]]
   ["-o" "--output FILE" "Path to desired output location of model"
    :default "target/model.edn"]])

(defn -main
  [& args]
  (let [{:keys [arguments errors options summary]} (cli/parse-opts args cli-opts)
        {:keys [help n output]} options
        usage (join \newline [usage "Options:" summary])
        input (first arguments)]
    (cond help         (exit 0 usage)
          (nil? input) (exit 1 (str "Input corpus file required\n" usage))
          errors       (exit 1 (join \newline (conj errors usage))))
    (println "Training model with n =" n)
    (let [corpus (with-open [in (clojure.java.io/reader input)] (doall (csv/read-csv in)))
          model (make-ngram-model n corpus)]
      (println (ngram-model-stats model))
      ;; TODO: can output training accuracy statistics
      (println "Output:" output)
      (spit output (prn-str model)))))
