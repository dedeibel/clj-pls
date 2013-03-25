(defproject clj-pls "0.2.2"
  :description "Small clojure wrapper using ini4j to parse and write .pls playlist files."
  :url "https://github.com/dedeibel/clj-pls"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.0"]
                 [org.ini4j/ini4j "0.5.2"]
                 ]
  :repositories [["mavenrepository" "http://repo1.maven.org/maven2/"]]
  :test-selectors {:default (every-pred (complement :acceptance) (complement :integration))
                   :integration :integration
                   :all (constantly true)}
  )
