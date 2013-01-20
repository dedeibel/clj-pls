# clj-pls

A Clojure library designed to read and write pls playlists which are
basically ini files.

http://en.wikipedia.org/wiki/PLS_%28file_format%29

## Usage

Using leiningen

Add [clj-pls "0.0.1"] as a dependency to your project.clj.

## Examples

```
(def playlist "[playlist]
File1=http://example.com:80
Title1=Example track 1
Length1=-1
File2=http://example.com/mysong.ogg
Title2=Podcast 1
Length2=195
NumberOfEntries=2
Version=2")

(pls/parse paylist)
{
  :entries 2
  :version "2"
  :files [
    {
      :title "Example track 1"
      :url "http://example.com:80"
      :length -1
    },
    {
      :title "Podcast 1"
      :url "http://example.com/mysong.ogg"
      :length 195
    },
  ]
}

(pls/parse (new File "paylist.pls"))
(pls/parse (new URL "http://example.com/awesome.pls"))
(pls/parse-file "paylist.pls")

TODO

(pls/write playlistmap)
(pls/write playlistmap writer)
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

* Allow better error handling, currently IOException and InvalidFileFormatException is thrown
* Allow writing of pls files
* Add lazyness option and assume file entries are in subsequent order

## Further reading

ini4j API documentation http://ini4j.sourceforge.net/apidocs/index.html

## License

Copyright © 2013 Benjamin Peter <BenjminPeter@arcor.de>

Distributed under the Eclipse Public License, the same as Clojure.

