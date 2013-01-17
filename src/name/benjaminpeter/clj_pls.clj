(ns name.benjaminpeter.clj-pls
  (import org.ini4j.Ini java.io.StringReader))

(defn- parse-files
  "Parses the files entries from the playlist section of the ini file"
  [playlist-section]

  ; TODO continue here
  (if (get playlist-section "Title1")
    [{
      :title "t1"
      :url "http://example.com/1"
      :length 1
      }]
    []))

(defn parse
  "Parses a .pls playlist as string and returns a map."
  [playlistString]
  (if (clojure.string/blank? playlistString)
    {
     :entries 0
     :version nil
     :files []
     }
    (let [ini (new Ini (new StringReader playlistString))
          playlist (.get ini "playlist")]
      {
       :entries (if-let [num-entries (get playlist "NumberOfEntries")] (Integer/parseInt num-entries) 0)
       :version (get playlist "Version")
       :files   (parse-files playlist)
       })))


(defn -trailing-digit [string]
  (if (and (not-empty string) (Character/isDigit (last string)))
    (conj (-trailing-digit (butlast string)) (last string))
    []))

(defn trailing-digit [string]
  (let [trailing-digits (-trailing-digit string)]
    (when (seq trailing-digits)
      (Integer/parseInt (apply str trailing-digits)))))

(defn entry-index [entry] 
  (trailing-digit (.getKey entry)))

(defn entry-key [entry] "title")

(defn entry-value [entry] "t")
