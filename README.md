# ngram-syllables

Syllable counting and detection using an n-gram language model.

## Usage

Training

```
Usage: lein run -m ngram-syllables.train [options] corpus
Options:
  -h, --help
  -n, --n GRAMS      1                 Number of grams
  -o, --output FILE  target/model.edn  Path to desired output location of model
```

Predictions

```
Usage: lein run -m ngram-syllables.predict [options] weight_1 ... weight_n
Options:
  -d, --delim DELIM  Empty space       Output syllable delimiter
  -h, --help
  -m, --model FILE   target/model.edn  Path to location of model
```

## Example

```
% ./train.sh
Training model with n = 3
17490 1-gram sequences
17489 2-gram sequences
7434 3-gram sequences
Output: target/model.edn
% echo "individualistically" | ./predict.sh --delim · 0.1 0.1 0.8
in·di·vid·u·al·is·ti·cal·ly
```

## License

Copyright © 2016-2017
