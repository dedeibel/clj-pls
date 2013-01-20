(ns name.benjaminpeter.clj-pls
  (import 
    org.ini4j.Ini
    java.io.StringReader
    [java.util Map Map$Entry]))

; (set! *warn-on-reflection* true)

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
  [playlist-section]
  (seq (vals 
         ; Hint: would be nicer without the doall ... but we would have to assume
         ;       all entries attributes (title/length) are in one "block"
         (doall (loop [entries playlist-section result (sorted-map)]
           (if (empty? entries)
             result
             (let [entry  (first entries)
                   eindex (entry-index entry)
                   ekey   (entry-key entry)
                   evalue (entry-value entry)]
               (if (some nil? [eindex ekey])  
                 (recur (rest entries) result)
                 (recur (rest entries) (assoc-in result [eindex ekey] evalue))))))))))

(defn parse
  "Parses a .pls playlist as string and returns a map."
  [playlistString]
  (if (clojure.string/blank? playlistString)
    {
     :entries 0
     :version nil
     :files nil
     }
    (let [ini (new Ini (new StringReader playlistString))
          playlist (.get ini "playlist")]
      {
       :entries (if-let [num-entries (get playlist "NumberOfEntries")] (Integer/parseInt num-entries) 0)
       :version (get playlist "Version")
       :files   (parse-entries playlist)
       })))
