(ns bitjorrent.core
  (:require [bitjorrent.bencode :as bencode])
  (:gen-class))

(def filename "/Users/ben/workspace/bitjorrent/test.torrent")
                                        ; /Users/ben/Downloads/prince.torrent")

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (bencode/decode filename))
                  

