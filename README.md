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

Generate syllable boundaries for some words not in the training corpus.

```
% ./train.sh
Training model with n = 3
17490 1-gram sequences
17489 2-gram sequences
7434 3-gram sequences
Output: target/model.edn
% head -n 20 resources/pokemon_names.txt | ./predict.sh --delim · 0.1 0.1 0.8
bulb·a·saur
i·vy·saur
ven·u·saur
char·man·der
char·mel·e·on
char·i·zard
squirt·le
war·tor·tle
blast·o·ise
ca·ter·pie
met·a·pod
but·ter·free
weed·le
ka·ku·na
bee·drill
pid·gey
pid·ge·ot·to
pid·ge·ot
rat·ta·ta
ra·ti·cate
```

## License

Copyright © 2016-2017
