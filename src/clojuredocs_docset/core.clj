(ns clojuredocs-docset.core
  (:require [clojure.java.jdbc :as j]
  			[clojure.java.jdbc.sql :as sql])
  (:import [org.jsoup Jsoup]
  		   [java.sql BatchUpdateException DriverManager
            PreparedStatement ResultSet SQLException Statement Types])
  (:gen-class))

(def sqlite-db {:classname "org.sqlite.JDBC"
			          :subprotocol "sqlite"
                :subname "docSet.dsidx"})

(defn populate-search-index [rows]
	(println "Populating index")
	(j/with-connection sqlite-db 
		(apply j/insert-records :searchIndex rows)))

(defn clear-search-index []
	(println "Clearing index")	
		(j/with-connection sqlite-db (j/do-commands "delete from searchIndex")))

(defn search-index-attributes [element]
	{:name (.text element) :type "Function" :path (.attr element "href")})

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  (let [html-content (slurp (str (System/getProperty "user.dir") "/clojure_core.html"))
  		document (Jsoup/parse html-content)
  		rows (map search-index-attributes (.select document ".function a"))]
  	(clear-search-index)
  	(populate-search-index rows))
  (println "Done."))
