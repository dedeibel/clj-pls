(ns name.benjaminpeter.clj-pls
  (:import 
    (org.ini4j Ini)
    (org.ini4j Config)
    (java.io StringReader)
    (org.ini4j Profile Profile$Section)
    (java.util Map Map$Entry)
    (java.io InputStream)
    (java.io File)
    (java.net URL)
    ))

;(set! *warn-on-reflection* true)

; Allows the definition of arbitrary case sections, sometimes the [playlist] section
; is written with upper case letters. Set this to true if you get any problems.
(def ^:dynamic *lower_case_sections* false)

; The following are not dynamic because there should not be a need to change them.
; If case insensitivity should be implemented the lowerCaseKeys option of ini4j is
; to be used.
(def ^String playlist-section-name  "playlist")
(def ^String number-of-entries-key  "NumberOfEntries")
(def ^String version-key            "Version")

(def ^String entry-title-key  "Title")
(def ^String entry-file-key   "File")
(def ^String entry-length-key "Length")

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
      (.startsWith keystring entry-title-key)   :title
      (.startsWith keystring entry-file-key)    :url
      (.startsWith keystring entry-length-key)  :length
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
                   eindex (entry-index  entry)
                   ekey   (entry-key    entry)
                   evalue (entry-value  entry)]
               (if (some nil? [eindex ekey])  
                 (recur (rest entries) result)
                 (recur (rest entries) (assoc-in result [eindex ekey] evalue)))))))))

(defn- create-entry-key [key-name index] 
  (str key-name index))

(defn- put-entry! [^Profile$Section playlist-section index file]
  (doto playlist-section
    (.add (create-entry-key entry-title-key   index)  (:title   file))
    (.add (create-entry-key entry-file-key    index)  (:url     file))
    (.add (create-entry-key entry-length-key  index)  (str (:length  file)))))

(defn- create-config []
  (doto (new Config) (.setLowerCaseSection *lower_case_sections*)))





(defmulti parse 
  "Parses a .pls playlist as stream, reader, file, url or string and returns a map of the content. See readme or tests for an example."
  class)

(derive java.io.InputStream ::input)
(derive java.io.Reader      ::input)
(derive java.io.File        ::input)
(derive java.net.URL        ::input)

(defmethod parse String [playlist-string]
  (parse (new StringReader playlist-string)))

(defmethod parse ::input
  [playlist-stream]
  (let [ini       (doto (new Ini) (.setConfig (create-config)) (.load playlist-stream))
        playlist  (.get ini playlist-section-name)]
    {
     :entries (if-let [num-entries (get playlist number-of-entries-key)] (Integer/parseInt num-entries) 0)
     :version (get playlist version-key)
     :files   (if-let [playlist playlist] (parse-entries playlist))
     }))

(defn parse-file
  "Parses a .pls playlist from a given filepath and returns a map of the content. See readme or tests for an example."
  [^String filepath]
  (parse (new File filepath)))



(defmulti write! 
  "Writes a .pls playlist as stream, writer or file and returns it. See readme or tests for an example."
  (fn [output-stream & more] (class output-stream)))

(derive java.io.OutputStream ::output)
(derive java.io.Writer       ::output)
(derive java.io.File         ::output)

(defmethod write! ::output
  [output-stream playlist & {:keys [version] :or {version "2"}}]
  (let [^Ini ini                          (new Ini)
        ^Profile$Section playlist-section (.add ini playlist-section-name)]
    (do 
      (.add playlist-section number-of-entries-key (str (count (:files playlist))))
      (doseq [[file index] (map list (:files playlist) (iterate inc 1))]
        (put-entry! playlist-section index file)
        )
      (.add playlist-section version-key version)
      (.store ini output-stream)
      output-stream
      )))

(defn write-file!
  "Writes a .pls playlist to the given filepath. See readme or tests for an example."
  [^String filepath & params]
  (do
    (apply write! (new File filepath) params)
    nil))

