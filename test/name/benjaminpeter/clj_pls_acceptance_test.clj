(ns name.benjaminpeter.clj-pls-acceptance-test
  (:use clojure.test
        name.benjaminpeter.clj-pls)
  (:import (java.net URL)
           (java.io  StringWriter)
           (java.io  IOException)))

(def playlist-http-url "http://metal-only.blitz-stream.de/listen.pls")
; Excepted content:
; One enry
; :title "METAL ONLY - www.metal-only.de - 24h Black Death Heavy Metal Rock und mehr!",
; :length 1,
; :url "http://metal-only.blitz-stream.de"

(deftest ^:acceptance parse-url-content
  (is 
    (try   (-> (new URL playlist-http-url) parse :files first :title (.startsWith "METAL ONLY"))
      (catch IOException e (do (println "Ignoring web test for url" playlist-http-url
                                        "because of a connection error" (class e) ":" (.getMessage e)) true)))))

(def acceptance-test-directory "resources/acceptance")
(def input-file-suffix ".pls")
(def expected-result-file-suffix ".expected")

(defn- read-from-file-safely [filename]
  (with-open
    [r (java.io.PushbackReader.
         (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))

(deftest ^:acceptance all-example-files-are-parsed-correctly
  (let [directory     (clojure.java.io/file acceptance-test-directory)
        files         (file-seq directory)
        pls-files     (filter #(-> % .getName (.endsWith input-file-suffix)) files)]
    (doseq [pls pls-files]
      (testing (.getName pls)
               (is (=
                    (read-from-file-safely (str (.getAbsoluteFile pls) expected-result-file-suffix))
                    (binding [name.benjaminpeter.clj-pls/*lower_case_sections* true]
                      (parse pls))))))))

(def write-test-expected-output 
"[playlist]
NumberOfEntries = 2
Title1 = First One
File1 = http://example.com/1
Length1 = -1
Title2 = Second
File2 = http://example.com/2.ogg
Length2 = 123
Version = 2

")
; Seems like ini4j adds two newlines at the end

(def write-test-playlist-input
  {
   :version "2"
   :entries 2
   :files [
           {
            :title "First One"
            :url "http://example.com/1"
            :length -1
            }
           {
            :title "Second"
            :url "http://example.com/2.ogg"
            :length 123
            }
           ]
   })

(deftest ^:acceptance writing-playlists
  (is (= write-test-expected-output
         (str (write! (new StringWriter) write-test-playlist-input)))))

