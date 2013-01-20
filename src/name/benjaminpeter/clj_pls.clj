(ns name.benjaminpeter.clj-pls
  (:use clojure.pprint)
  (:import 
    org.ini4j.Ini
    org.ini4j.Config
    java.io.StringReader
    [org.ini4j Profile Profile$Section]
    [java.util Map Map$Entry]
    [java.io InputStream]
    [java.io File]
    [java.net URL]
    ))

; (set! *warn-on-reflection* true)

; Allows the definition of arbitrary case sections, sometimes the [playlist] section
; is written with upper case letters. Set this to true if you get any problems.
(def ^:dynamic *lower_case_sections* false)

(defn trailing-digit-seq [string]
  (loop [string (seq string) result nil]
    (if (or (empty? string) (not (Character/isDigit (^Character last string))))
      result
      (recur (butlast string) (conj result (last string))))))

(defn trailing-digit [string]
  (let [trailing-digits (trailing-digit-seq string)]
    (when (seq trailing-digits)
      (Integer/parseInt (apply str trailing-digits)))))

(defn entry-index [^Map$Entry entry] 
  (trailing-digit (.getKey entry)))

(defn entry-key [^Map$Entry entry]
  (let [^String keystring (.getKey entry)]
    (cond
      ; Hint: Implement case ignore match here YAGNI
      (.startsWith keystring "Title")   :title
      (.startsWith keystring "File")    :url
      (.startsWith keystring "Length")  :length
      ; Hint: Implement error messages on illegal keys here YAGNI
      1 nil)))

(defn entry-value [^Map$Entry entry]
  (if (= :length (entry-key entry))
    (Integer/parseInt (.getValue entry))
    (.getValue entry)))

(defn- parse-entries
  "Parses the file entries from the playlist section of the ini file"
  [^Profile$Section playlist-section]
  (seq (vals 
         (loop [entries (.entrySet playlist-section) result (sorted-map)]
                  (if (empty? entries)
                    result
                    (let [entry  (first entries)
                          eindex (entry-index entry)
                          ekey   (entry-key entry)
                          evalue (entry-value entry)]
                      (if (some nil? [eindex ekey])  
                        (recur (rest entries) result)
                        (recur (rest entries) (assoc-in result [eindex ekey] evalue)))))))))

(defmulti parse 
  "Parses a .pls playlist as stream, reader, file, url or string and returns a map of the content. See readme or tests for an example."
  class)

(derive java.io.InputStream ::input)
(derive java.io.Reader      ::input)
(derive java.io.File        ::input)
(derive java.net.URL        ::input)

(defmethod parse String [playlistString]
    (parse (new StringReader playlistString)))

(defn- create-config []
  (doto (new Config) (.setLowerCaseSection *lower_case_sections*)))

(defmethod parse ::input
  [playlistStream]
  (let [ini       (doto (new Ini) (.setConfig (create-config)) (.load playlistStream))
        playlist  (.get ini "playlist")]
    {
     :entries (if-let [num-entries (get playlist "NumberOfEntries")] (Integer/parseInt num-entries) 0)
     :version (get playlist "Version")
     :files   (if-let [playlist playlist] (parse-entries playlist))
     }))

(defn parse-file
  "Parses a .pls playlist from a given filepath and returns a map of the content. See readme or tests for an example."
  [^String filepath]
    (parse (new File filepath)))

