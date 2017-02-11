#!/bin/sh
MODEL=target/model.edn

lein run -m ngram-syllables.predict --model $MODEL "$@"
