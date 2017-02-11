#!/bin/sh
CORPUS=resources/corpus.csv
N=3
MODEL=target/model.edn

lein run -m ngram-syllables.train -n $N --output $MODEL $CORPUS
