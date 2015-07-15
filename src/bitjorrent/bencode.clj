(ns bitjorrent.bencode
  (:require [clojure.java.io :as io])
  (:gen-class))

(def filename "/Users/ben/workspace/bitjorrent/test/test.torrent")

; could be recursive
(defn parse-list
  [])

; could be recursive
;; (defn parse-dict
;;   "parse elements after d, but before e"
;;   [stream]
;;   {parse-key parse-value})

;; not recursive
(defn parse-int
  ([stream]
   (parse-int stream ""))
  ([stream acc]
   (let [n (first stream)]
    (if (and (not (= n \e)) (not (= n \:)))
      (recur (rest stream) (str acc n))
      [stream acc]))))

;; not recursive
(defn parse-bytes
  [stream]
  (let [[s num] (parse-int stream)]
    (loop [newstream (rest s) ;; rest to get rid of colon
           i (dec (read-string num)) ;; dec to get rid of colon
           acc ""]
      (if (> i 0)
        (recur (rest newstream) (dec i) (str acc (first newstream)))
        [(rest newstream) (str acc (first newstream))]))))

(defn parse-token
  [stream]
  (let [first-char (first stream)]
    (cond
;      (= first-char \d) ()
      (= first-char \i) (let [[newstream num] (parse-int (rest stream))]
                          (println num)
                          newstream)
      (re-matches #"\d" (str first-char)) (let [[newstream bytes] (parse-bytes stream)]
                                            (println bytes)
                                            newstream)
      :else (when (not (empty? stream))
              (recur (rest stream))))))

(defn parse-tokens
  [stream]
  (println (first stream))
  (when (not (empty? stream))
    (recur (parse-token stream))))

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

