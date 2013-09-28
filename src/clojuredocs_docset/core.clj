(ns clojuredocs-docset.core
  (:require [clojure.java.jdbc :as j]
  			[clojure.java.jdbc.sql :as sql])
  (:use [clojure.java.shell :only [sh]]
        [clojure.java.io :only [resource]])
  (:import [org.jsoup Jsoup]
  		   [java.sql BatchUpdateException DriverManager
            PreparedStatement ResultSet SQLException Statement Types])
  (:gen-class))

(def user-dir (System/getProperty "user.dir"))
(def conf (read-string (slurp (resource "config.clj"))))

(def sqlite-db {:classname "org.sqlite.JDBC"
			          :subprotocol "sqlite"
                :subname (:db-file-path conf)})

(defn print-progress [percent text]
  (let [x (int (/ percent 2))
        y (- 50 x)]
    (print "\r" (apply str (repeat 100 " ")));clear existing text
    (print "\r" "["
      (apply str (concat (repeat x "=") [">"] (repeat y " ")))
      (str "] " percent "%") text)
    (when (= 100 percent) (println))
  (flush)))

(defn mirror-clojuredocs []
  (print-progress 15 "Mirroring clojuredocs.org/clojure_core")
  (sh apply (:httrack conf)))

(defn copy-html-to-docset []
  (print-progress 50 "Copying clojuredocs.org to docset")
  (sh apply (:mkdir-docset conf))
  (sh apply (:cp-html-to-docset conf)))

(defn clear-search-index []
  (print-progress 60 "Clearing index")
  (j/with-connection sqlite-db 
    (j/do-commands "DROP TABLE IF EXISTS searchIndex"
                   "CREATE TABLE searchIndex(id INTEGER PRIMARY KEY, name TEXT, type TEXT, path TEXT)"
                   "CREATE UNIQUE INDEX anchor ON searchIndex (name, type, path)")))

(defn populate-search-index [rows]  
  (j/with-connection sqlite-db 
    (apply j/insert-records :searchIndex rows)))

(defn search-index-attributes [element]
  {:name (.text element) :type "Function" :path (.attr element "href")})

(defn generate-search-index []
  (print-progress 75 "Generating index")
  (let [html-content (slurp (str user-dir "/" (:html-file-path conf)))
        document (Jsoup/parse html-content)
        rows (map search-index-attributes (.select document ".function a"))]            
    (populate-search-index rows)))

(defn generate-docset []
  (mirror-clojuredocs)
  (copy-html-to-docset)
  (clear-search-index)
  (generate-search-index)
  (print-progress 100 "Done."))

(defn -main
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (generate-docset)
  (shutdown-agents))
