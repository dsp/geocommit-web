(ns worker.repository
  (:require [me.raynes.conch :refer [programs with-programs let-programs]])
  (:import (java.security MessageDigest)))

(defn sha1 [obj]
  (let [bytes (.getBytes (with-out-str (pr obj)))] 
    (apply vector (.digest (MessageDigest/getInstance "SHA1") bytes))))

(defn repo-dir
  [repo-id]
  (str "/tmp/geocommit/" (sha1 repo-id)))

(defprotocol Repository
  (clone [this])
  (geocommits [this] [this commits]))

(deftype GitHub [id url]
  Repository
  (clone
    [this]
    (programs git)
    (git "clone" url (repo-dir id)))
  (geocommits
    [this]
    (vec ["foo" "bar"]))
  (geocommits
    [this commits]
    (vec ["foo"])))

(deftype BitBucket [id url]
  Repository
  (clone
    [this]
    nil)
  (geocommits
    [this]
    (vec ["foo" "bar"]))
  (geocommits
    [this commits]
    (vec ["foo"])) )

(defmulti create-repo
          (fn [id url]
              (or
                (and (.startsWith id "bitbucket.org") ::bitbucket)
                (and (.startsWith id "github.com") ::github))))

(defmethod create-repo ::github [id url] (GitHub. id url))
(defmethod create-repo ::bitbucket [id url] (BitBucket. id url))
(defmethod create-repo ::default [id url] nil)
