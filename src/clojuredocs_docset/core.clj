(ns clojuredocs-docset.core
  (:require [clojure.java.jdbc :as j]
  			[clojure.java.jdbc.sql :as sql])
  (:use [clojure.java.shell :only [sh]]
        [clojure.java.io :only [reader writer file resource copy]])
  (:import [org.jsoup Jsoup]
           [org.apache.commons.io FileUtils]
  		     [java.sql BatchUpdateException DriverManager
            PreparedStatement ResultSet SQLException Statement Types]
           [java.io File])
  (:gen-class))

(def user-dir (System/getProperty "user.dir"))
(def conf (read-string (slurp (resource "config.clj"))))

(defn file-ref [file-name]
  (file user-dir file-name))

(defn resource-copy [src dest]
  (FileUtils/copyURLToFile (resource src) (file-ref dest)))

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
  (apply sh (:httrack conf)))

(defn create-docset-template []
    (.mkdirs (file-ref (:docset-template conf)))
    (resource-copy "icon.png" "clojure-docs.docset/icon.png")
    (resource-copy "Info.plist" "clojure-docs.docset/Contents/Info.plist"))

(defn copy-html-to-docset []
  (print-progress 50 "Copying clojuredocs.org to docset")
  (FileUtils/copyDirectoryToDirectory (file-ref (:httrack-clojuredocs conf)) (file-ref (:docset-template conf))))

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
  (let [html-content (slurp (str user-dir "/" (:index-html-path conf)))
        document (Jsoup/parse html-content)
        rows (map search-index-attributes (.select document ".function a"))]            
    (populate-search-index rows)))

(defn generate-docset []
  ; (mirror-clojuredocs)
  (create-docset-template)
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
