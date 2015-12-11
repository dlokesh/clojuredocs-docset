(defproject clojuredocs-docset "0.1.1"
  :description "Dash docset generator for ClojureDocs.org"
  :url "http://github.com/dlokesh/clojuredocs-docset"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
        				 [org.jsoup/jsoup "1.8.3"]
        				 [org.clojure/java.jdbc "0.3.0-alpha4"]
        				 [org.xerial/sqlite-jdbc "3.7.2"]
                 [commons-io "2.4"]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  :plugins [[lein-midje "3.0.0"]]
  :main clojuredocs-docset.core)
