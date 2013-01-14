# clj-pls

A Clojure library designed to read and write pls playlists which are
basically ini files.

http://en.wikipedia.org/wiki/PLS_%28file_format%29

## Usage

Using leiningen

Add [clj-pls "0.0.1"] as a dependency to your project.clj.

## Examples

    [playlist]
    File1=http://example.com:80
    Title1=Example track 1
    Length1=-1
    File2=http://example.com/mysong.ogg
    Title2=Podcast 1
    Length2=195
    NumberOfEntries=2
    Version=2
    
    (pls/parse "paylist.pls")
    {
      :expected-entries 2
      :version 2
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

    (pls/parse-files "paylist.pls")
    [
      {
        :title "Example track 1"
        :url "http://example.com:80"
    ...
    ]

    (pls/write playlistmap)
    [playlist]
    File1=http://example.com:80
    ...

    (pls/write playlistmap writer)

## TODOs

* Implement
* Test various pls files
* Use lazy seqs

## License

Copyright © 2013 Benjamin Peter

Distributed under the Eclipse Public License, the same as Clojure.
