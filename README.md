# clj-pls

A Clojure library designed to read and write pls playlists which are
basically ini files.

http://en.wikipedia.org/wiki/PLS_%28file_format%29

## Usage

Using leiningen

Add [clj-pls "0.2.0"] as a dependency to your project.clj.

## Examples

Input:

```
(require ['name.benjaminpeter.clj-pls :as 'pls])

(def playlist-str "[playlist]
File1=http://example.com:80
Title1=Example track 1
Length1=-1
File2=http://example.com/mysong.ogg
Title2=Podcast 1
Length2=195
NumberOfEntries=2
Version=2")

(pls/parse playlist-str)
; {:entries 2,
;  :version "2",
;  :files [
;   {:url "http://example.com:80",        :title "Example track 1", :length -1}
;   {:url "http://example.com/mysong.ogg",:title "Podcast 1",       :length 195}]}

(pls/parse (new java.io.ByteArrayInputStream (.getBytes playlist-str "UTF8")))
(pls/parse (new java.io.File "paylist.pls"))
(pls/parse (new java.net.URL "http://example.com/awesome.pls"))
(pls/parse-file "paylist.pls")
```

Output:

```
(require ['name.benjaminpeter.clj-pls :as 'pls])

(def playlist
  {:entries 2,
   :version "2",
   :files [
    {:url "http://example.com:80",        :title "Example track 1", :length -1}
    {:url "http://example.com/mysong.ogg",:title "Podcast 1",       :length 195}]})

(pls/write! (new java.io.StringWriter) playlist)
(pls/write! (new java.io.ByteArrayOutputStream) playlist)
(pls/write! (new java.io.File "/tmp/clj-pls-t1.pls") playlist)
(pls/write-file! "/tmp/clj-pls-t2.pls" playlist)
```

## Running tests

```
$ lein test
```

Running other tests:

```
$ lein test :integration|:acceptance|:all
```

## TODOs

* Maybe extract sub namespaces
* Allow better error handling, currently IOException and InvalidFileFormatException is thrown
* Add lazyness option and assume file entries are in subsequent order

## Further reading

ini4j API documentation http://ini4j.sourceforge.net/apidocs/index.html

## License

Copyright © 2013 Benjamin Peter <BenjminPeter@arcor.de>

Distributed under the Eclipse Public License, the same as Clojure.

