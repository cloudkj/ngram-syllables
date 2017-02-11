(ns ngram-syllables.predict
  (:require [clojure.edn :as edn]
            [clojure.string :refer [join trim]]
            [clojure.tools.cli :as cli]
            [ngram-syllables.core :refer :all]
            [ngram-syllables.util :refer :all]))

(def usage
  (str "Usage: lein run -m " (ns-name *ns*) " [options] weight_1 ... weight_n"))

(def cli-opts
  [["-d" "--delim DELIM" "Output syllable delimiter"
    :default \space
    :default-desc "Empty space"]
   ["-h" "--help"]
   ["-m" "--model FILE" "Path to location of model"
    :id :path
    :default "target/model.edn"]])

(defn -main
  [& args]
  (let [{:keys [arguments errors options summary]} (cli/parse-opts args cli-opts)
        {:keys [delim help path]} options
        usage (join \newline [usage "Options:" summary])
        weights (map #(Double/parseDouble %) arguments)]
    (cond help                               (exit 0 usage)
          (empty? weights)                   (exit 1 usage)
          errors                             (exit 1 (join \newline (conj errors usage)))
          (not (every? #(<= 0 % 1) weights)) (exit 1 (str "Each weight must be between 0.0 and 1.0\n" usage))
          (not= (reduce + weights) 1.0)      (exit 1 (str "Weights must sum to 1.0\n" usage)))
    (let [model (edn/read-string (slurp path))
          n (count weights)
          m (apply max (keys model))]
      (when (> n m)
        (exit 1 (str "Model only supports up to " m "-grams\n" usage)))
      (doseq [line (line-seq (java.io.BufferedReader. *in*))]
        (->> (predict weights model (trim line))
             (join delim)
             (println))))))
