(ns name.benjaminpeter.clj-pls-test
  (:use clojure.test
        name.benjaminpeter.clj-pls))

(deftest hash-with-number-of-entries-and-version-is-returned
  (is (= {
          :entries 0
          :version nil
          :files []
          }
         (parse ""))))

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
          :files []
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

(deftest entries-are-parsed-as-array
  (is (= {
          :title "t1"
          :url "http://example.com/1"
          :length 1
        }
        (first (:files (parse playlist-with-three-entries)))))
  )

