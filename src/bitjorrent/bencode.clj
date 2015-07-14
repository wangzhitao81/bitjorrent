(ns bitjorrent.bencode
  (:require [clojure.java.io :as io])
  (:gen-class))

(def filename "/Users/ben/workspace/bitjorrent/test/test.torrent")

; could be recursive
(defn parse-list
  [])

; could be recursive
(defn parse-dict
  "parse elements after d, but before e"
  [stream]
  )

; not recursive
(defn parse-int
  ([stream]
   (parse-int stream ""))
  ([stream acc]
   (let [n (first stream)]
    (if (and (not (= n \e)) (not (= n \:)))
      (recur (rest stream) (str acc n))
      [stream acc]))))

; not recursive
(defn parse-bytes
  ([stream]
   (parse-bytes stream 0))
  ([stream length]
   (let [[s num] (parse-int stream)]
     (loop [newstream s
            i (read-string num)
            acc ""]
       (if (> i 0)
         (recur (rest newstream) (dec i) (str acc (first newstream)))
         [newstream (str acc (first s))])))))

(defn parse-tokens
  [stream]
  (println (first stream))

  ;; parse each type in their own funcs
  ;; each func should return the new stream, and the most updated map/list/nums
  (cond 
    (= (first stream) \i) (let [[newstream num] (parse-int (rest stream))]
                            (println num)
                            (recur newstream))
    (re-matches #"\d" (str (first stream))) (let [[s bytes] (parse-bytes stream)]
                                              (println bytes)
                                              (recur s))
    (not (empty? stream)) (recur (rest stream))))


(defn file-stream
  [uri]
  (let [in (io/input-stream uri)]
    (letfn [(f [ins]
              (lazy-seq
               (let [byte (.read ins)]
                 (when (not= byte -1)
                   (cons byte (f ins))))))]
      (f in))))

(defn decode
  [path]
  (let [char-stream (map char (file-stream path))]
    (parse-tokens char-stream)))

