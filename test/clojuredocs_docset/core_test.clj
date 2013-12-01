(ns clojuredocs-docset.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
  			[clojure.java.jdbc :as j]
            [clojuredocs-docset.core :refer :all])
  (:import  [org.jsoup.nodes Element Attributes Attribute]
  			[org.jsoup.parser Tag]))

(facts "about search index"
	(fact "it should construct search index attributes"
		(let [tag (Tag/valueOf "a")
			  attributes (Attributes.)
			  _ (.put attributes "href" "clojure_core/clojure.html")
			  element (Element. tag "uri" attributes)
			  _ (.text element "core")]
		(search-index-attributes element) => {:name "core" :type "Function" :path "clojuredocs.org/clojure_core/clojure.html"})))