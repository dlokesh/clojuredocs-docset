{
	:db-file-path "clojure-docs.docset/Contents/Resources/docSet.dsidx",
	:html-file-path "clojure-docs.docset/Contents/Resources/Documents/clojuredocs.org/clojure_core.html",
	:httrack ["httrack" "http://clojuredocs.org/clojure_core" "-n" "--path" "httrack-clojuredocs.org" "-http://clojuredocs.org/clojure_core/1.2.0/*" "-http://clojuredocs.org/clojure_core/1.3.0/*" "-http://clojuredocs.org/examples/*" "-http://clojuredocs.org/clojure_contrib/*" "-http://clojuredocs.org/profile/*" "-http://clojuredocs.org/*search*" "-http://clojuredocs.org/ac_search/*" "-http://clojuredocs.org/feed/*" "-http://clojuredocs.org/management/*" "-http://clojuredocs.org/ring/*"],
	:mkdir-docset ["mkdir" "-p" "clojure-docs.docset/Contents/Resources/Documents"],
	:cp-html-to-docset ["cp" "-r" "httrack-clojuredocs.org/clojuredocs.org" "clojure-docs.docset/Contents/Resources/Documents"]
}