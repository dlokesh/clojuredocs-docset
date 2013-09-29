## [Dash][dash] docset generator for [ClojureDocs.org][clojuredocs]

Performs the following:

* Mirror clojuredocs.org/clojure_core (around 17mb) using [HTTrack][httrack]
* Copy html content to default dash docset template
* Parse all functions from clojure_core.html
* Populate searchIndex in docSet.dsidx (sqlite db)

## Installation

Install the following dependencies:

    $ brew install httrack
    $ brew install leiningen

## Usage

Checkout source and run:

    $ lein run
    
You can now import clojure-docs.docset into [Dash][dash].

## License

Copyright © 2013 Lokeshwaran

Distributed under the Eclipse Public License, the same as Clojure.

[clojuredocs]: http://clojuredocs.org
[dash]: http://kapeli.com/dash
[httrack]: http://www.httrack.com
