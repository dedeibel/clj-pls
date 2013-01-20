(ns name.benjaminpeter.clj-pls-test
  (:use clojure.test
        name.benjaminpeter.clj-pls)
  (:import [java.io ByteArrayInputStream]))

(deftest a-hash-with-number-of-entries-and-version-is-returned
  (testing "Parsing an empty string."
           (is (= {
                   :entries 0
                   :version nil
                   :files nil
                   }
                  (parse ""))))
  (testing "Parsing an empty input stream."
           (is (= {
                   :entries 0
                   :version nil
                   :files nil
                   }
                  (parse (new ByteArrayInputStream (.getBytes "" "UTF8")))))))

(def empty-playlist "[playlist]
                     NumberOfEntries=0
                     Version=2")

(def playlist-missing-version-definition "[playlist]
                                          NumberOfEntries=0")

(deftest version-is-parsed-from-playlist
  (testing "A normal version definition results in a it's value."
           (is (= {
                   :entries 0
                   :version "2"
                   :files []
                   }
                  (parse empty-playlist))))
  (testing "A  missing version definition results in a nil value."
           (is (= {
                   :entries 0
                   :version nil
                   :files []
                   }
                  (parse playlist-missing-version-definition)))))

(deftest version-is-parsed-from-playlist
  (is (= {
          :entries 0
          :version "2"
          :files nil
          }
         (parse empty-playlist))))

(def playlist-with-num-entries-one "[playlist]
                                    NumberOfEntries=1")

(def playlist-with-num-entries-key-missing "[playlist]")

(deftest number-of-entires-is-parsed
  (testing "The entries string is present and it's value is returned."
           (is (= 1 (:entries (parse playlist-with-num-entries-one)))))
  (testing "The entries string is missing and zero is returned if no files are listed."
           (is (= 0 (:entries (parse playlist-with-num-entries-key-missing))))))

(deftest trailing-digit-seq-test
  (is (= "1"  (apply str (trailing-digit-seq "some1"))))
  (is (= "2"  (apply str (trailing-digit-seq "some2"))))
  (is (= "10" (apply str (trailing-digit-seq "some10"))))
  (is (= ""   (apply str (trailing-digit-seq "some"))))
  )

(deftest trailing-digit-test
  (is (= 1 (trailing-digit "some1")))
  (is (= 10 (trailing-digit "some10")))
  (is (= nil (trailing-digit "some")))
  )

(def playlist-with-three-entries "[playlist]
                                  File1=http://example.com/1
                                  Title1=t1
                                  Length1=1
                                  File2=http://example.com/2
                                  Title2=t2
                                  Length2=2
                                  File3=http://example.com/3
                                  Title3=t3
                                  Length3=3
                                  ")

(deftest entry-index-test
  (is (= 1 (entry-index (new java.util.AbstractMap$SimpleImmutableEntry "File1" "http://exampl.com/1"))))
  (is (= 2 (entry-index (new java.util.AbstractMap$SimpleImmutableEntry "File2" "http://exampl.com/1")))))

(deftest entry-key-test
  (testing "Keys are parsed correctly"
           (is (= :url     (entry-key (new java.util.AbstractMap$SimpleImmutableEntry "File2" ""))))
           (is (= :title   (entry-key (new java.util.AbstractMap$SimpleImmutableEntry "Title1" ""))))
           (is (= :length  (entry-key (new java.util.AbstractMap$SimpleImmutableEntry "Length" "")))))
  (testing "An illegal key is ignored."
           (is (nil? (entry-key (new java.util.AbstractMap$SimpleImmutableEntry "bogus" ""))))
           (is (nil? (entry-key (new java.util.AbstractMap$SimpleImmutableEntry "file1" ""))))
           ))

(deftest entries-are-parsed-as-array
  (testing "The first entry in the playlist is parsed correctly"
           (is (= {
                   :title "t1"
                   :url "http://example.com/1"
                   :length 1
                   }
                  (first (:files (parse playlist-with-three-entries))))))
  (testing "The last entry in the playlist is parsed correctly"
           (is (= {
                   :title "t3"
                   :url "http://example.com/3"
                   :length 3
                   }
                  (nth (:files (parse playlist-with-three-entries)) 2)))))

(def one-entry-pls-file-path "resources/one-entry-playlist.pls")

(deftest ^:integration file-input-is-working
  (testing "The file is present and can be read."
           (is (not (empty? (slurp one-entry-pls-file-path)))))
  (testing "Parsing the playlist from file is working too."
           (= "My Favorite Song" (:title (first (:files (parse-file one-entry-pls-file-path)))))))

