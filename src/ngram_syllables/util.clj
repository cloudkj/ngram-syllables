(ns ngram-syllables.util)

(defn exit
  [status msg]
  (binding [*out* *err*]
    (println msg))
  (System/exit status))
